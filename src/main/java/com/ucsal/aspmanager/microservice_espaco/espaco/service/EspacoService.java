package com.ucsal.aspmanager.microservice_espaco.espaco.service;

import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.CreateEspacoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.CreateSolicitacaoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.UpdateEspacoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.UpdateSolicitacaoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.response.EspacoResponse;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.response.SolicitacaoResponse;
import com.ucsal.aspmanager.microservice_espaco.espaco.mapper.EspacoMapper;
import com.ucsal.aspmanager.microservice_espaco.espaco.mapper.SolicitacaoMapper;
import com.ucsal.aspmanager.microservice_espaco.espaco.model.Espaco;
import com.ucsal.aspmanager.microservice_espaco.espaco.model.SolicitacaoEspaco;
import com.ucsal.aspmanager.microservice_espaco.espaco.repository.EspacoRepository;
import com.ucsal.aspmanager.microservice_espaco.espaco.repository.SolicitacaoEspacoRepository;
import com.ucsal.aspmanager.microservice_espaco.shared.model.enums.StatusRegistro;
import com.ucsal.aspmanager.microservice_espaco.shared.model.enums.StatusSolicitacao;
import com.ucsal.aspmanager.microservice_espaco.shared.model.enums.TipoEspaco;
import com.ucsal.aspmanager.microservice_espaco.shared.service.ServiceBase;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EspacoService implements ServiceBase<Long, CreateEspacoRequest, UpdateEspacoRequest, EspacoResponse> {

    private final EspacoRepository espacos;
    private final SolicitacaoEspacoRepository solicitacoes;
    private final SolicitacaoMapper solicitacaoMapper;
    private final EspacoMapper espacoMapper;

    public EspacoService(EspacoRepository espacos, SolicitacaoEspacoRepository solicitacoes, SolicitacaoMapper solicitacaoMapper, EspacoMapper espacoMapper) {
        this.espacos = espacos;
        this.solicitacoes = solicitacoes;
        this.solicitacaoMapper = solicitacaoMapper;
        this.espacoMapper = espacoMapper;
    }

    @Override
    @Transactional
    public EspacoResponse criar(CreateEspacoRequest request) {
        List<Long> idsSoftwares = request.softwares() == null ? Collections.emptyList() : request.softwares();

        Espaco espaco = espacoMapper.toEntity(request);
        espaco.setIdEscola(request.idEscola());

        if (request.tipoEspaco().equals(TipoEspaco.LABORATORIO)) {
            espaco.setSoftwaresIds(idsSoftwares);
        } else {
            espaco.setSoftwaresIds(Collections.emptyList());
        }

        return espacoMapper.toResponse(espacos.save(espaco));
    }

    @Override
    public Page<EspacoResponse> buscarTodos(Pageable filtros) {
        return espacos.findAll(filtros).map(espacoMapper::toResponse);
    }

    @Override
    public EspacoResponse buscar(Long id) {
        Espaco espaco = espacos.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Espaço não encontrado!"));
        return espacoMapper.toResponse(espaco);
    }

    @Override
    @Transactional
    public EspacoResponse atualizar(Long id, UpdateEspacoRequest request) {
        Espaco espaco = espacos.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Espaço não encontrado!"));

        espacoMapper.updateEntity(request, espaco);
        espaco.setIdEscola(request.idEscola());

        return espacoMapper.toResponse(espaco);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Espaco espaco = espacos.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Espaço não encontrado!"));

        if (solicitacoes.existsByEspaco_Id(id)) {
            espaco.setStatusRegistro(StatusRegistro.INATIVO);
            return;
        }
        espacos.delete(espaco);
    }

    @Transactional
    public SolicitacaoResponse criarSolicitacao(Long professorId, CreateSolicitacaoRequest request) {
        Espaco espaco = espacos.findById(request.idEspaco()).orElseThrow(() ->
                new EntityNotFoundException("Espaço não encontrado!"));

        SolicitacaoEspaco solicitacaoEspaco = solicitacaoMapper.toEntity(request);
        solicitacaoEspaco.setProfessorId(professorId);
        solicitacaoEspaco.setEspaco(espaco);
        solicitacaoEspaco.setStatusSolicitacao(StatusSolicitacao.PENDENTE);

        SolicitacaoEspaco salva = solicitacoes.saveAndFlush(solicitacaoEspaco);
        return solicitacaoMapper.toResponse(salva);
    }

    public Page<EspacoResponse> buscarDisponiveis(LocalDate dataUso, LocalTime horaInicio, LocalTime horaFim, Pageable filtros) {
        if (dataUso == null || horaInicio == null || horaFim == null) {
            throw new IllegalArgumentException("Data e horários são obrigatórios!");
        }
        if (!horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("A hora de início deve ser anterior à hora de fim!");
        }

        return espacos.buscarDisponiveisNoPeriodo(dataUso, horaInicio, horaFim, StatusRegistro.ATIVO, StatusSolicitacao.APROVADO, filtros)
                .map(espacoMapper::toResponse);
    }

    public Page<SolicitacaoResponse> buscarSolicitacao(Pageable filtros) {
        return solicitacoes.findAll(filtros).map(solicitacaoMapper::toResponse);
    }

    public Page<SolicitacaoResponse> buscarMinhasSolicitacoes(Long professorId, Pageable filtros) {
        return solicitacoes.findByProfessorId(professorId, filtros).map(solicitacaoMapper::toResponse);
    }

    public SolicitacaoResponse buscarSolicitacao(Long id) {
        SolicitacaoEspaco solicitacaoEspaco = solicitacoes.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Solicitação não encontrada!"));
        return solicitacaoMapper.toResponse(solicitacaoEspaco);
    }

    @Transactional
    public SolicitacaoResponse atualizarSolicitacao(Long id, UpdateSolicitacaoRequest request) {
        SolicitacaoEspaco solicitacaoEspaco = solicitacoes.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Solicitação não encontrada!"));

        if (request.idEspaco() != null && !request.idEspaco().equals(solicitacaoEspaco.getEspaco().getId())) {
            Espaco novoEspaco = espacos.findById(request.idEspaco()).orElseThrow(() ->
                    new EntityNotFoundException("Espaço não encontrado!"));
            solicitacaoEspaco.setEspaco(novoEspaco);
        }

        if (request.idProfessor() != null) {
            solicitacaoEspaco.setProfessorId(request.idProfessor());
        }

        solicitacaoEspaco.setStatusSolicitacao(request.statusSolicitacao());
        return solicitacaoMapper.toResponse(solicitacaoEspaco);
    }

    @Transactional
    public void deletarSolicitacao(Long id) {
        try {
            solicitacoes.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Solicitação não encontrada!");
        }
    }
}