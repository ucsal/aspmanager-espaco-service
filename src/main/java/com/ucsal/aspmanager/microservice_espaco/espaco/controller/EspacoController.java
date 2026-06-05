package com.ucsal.aspmanager.microservice_espaco.espaco.controller;

import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.CreateEspacoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.CreateSolicitacaoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.UpdateEspacoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.UpdateSolicitacaoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.response.EspacoResponse;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.response.SolicitacaoResponse;
import com.ucsal.aspmanager.microservice_espaco.espaco.service.EspacoService;
import com.ucsal.aspmanager.microservice_espaco.shared.controller.AbstractCrudController;
import com.ucsal.aspmanager.microservice_espaco.shared.model.dto.ErroApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/espaco")
@Tag(name = "Espaços Acadêmicos", description = "Gestão de espaços acadêmicos e solicitações de reserva")
public class EspacoController extends AbstractCrudController<Long, CreateEspacoRequest, UpdateEspacoRequest, EspacoResponse> {

    private final EspacoService espacoService;

    public EspacoController(EspacoService espacoService) {
        super(espacoService);
        this.espacoService = espacoService;
    }

    @Override
    protected URI location(EspacoResponse espaco, UriComponentsBuilder uriBuilder) {
        return uriBuilder.path("/api/v1/espaco/{id}").buildAndExpand(espaco.id()).toUri();
    }

    @PostMapping("/solicitacao")
    @Operation(operationId = "createEspacoSolicitacao", summary = "Criar solicitação de reserva", description = "Permite ao professor solicitar reserva de um espaço acadêmico para data e horário específicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para criação da solicitação", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Professor ou espaço não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<SolicitacaoResponse> criarSolicitacao(
            @RequestHeader(value = "X-User-Id") Long professorId,
            @Valid @RequestBody CreateSolicitacaoRequest createSolicitacao,
            UriComponentsBuilder uriBuilder) {

        SolicitacaoResponse solicitacaoResponse = espacoService.criarSolicitacao(professorId, createSolicitacao);
        URI uri = espacoLocation(solicitacaoResponse, uriBuilder);

        return ResponseEntity.created(uri).body(solicitacaoResponse);
    }

    protected URI espacoLocation(SolicitacaoResponse solicitacao, UriComponentsBuilder uriBuilder) {
        return uriBuilder.path("/api/v1/espaco/solicitacao/{id}").buildAndExpand(solicitacao.id()).toUri();
    }

    @GetMapping("/solicitacao")
    @Operation(operationId = "listEspacoSolicitacoes", summary = "Listar solicitações de reserva", description = "Retorna lista paginada de solicitações de reserva de espaços.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<Page<SolicitacaoResponse>> buscarSolicitacao(@ParameterObject Pageable filtros) {
        return ResponseEntity.ok(espacoService.buscarSolicitacao(filtros));
    }

    @GetMapping("/solicitacao/minhas")
    @Operation(operationId = "listMinhasEspacoSolicitacoes", summary = "Listar minhas solicitações de espaço", description = "Retorna lista paginada das solicitações do professor autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Professor autenticado não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Usuário autenticado inválido para esta operação", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<Page<SolicitacaoResponse>> buscarMinhasSolicitacoes(
            @RequestHeader(value = "X-User-Id") Long professorId,
            @ParameterObject Pageable filtros) {
        return ResponseEntity.ok(espacoService.buscarMinhasSolicitacoes(professorId, filtros));
    }

    @GetMapping("/disponiveis")
    @Operation(operationId = "listEspacosDisponiveis", summary = "Listar espaços disponíveis no período", description = "Consulta disponibilidade de espaços por data e intervalo de horários, respeitando reservas aprovadas e status do espaço.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Data/hora inválidas ou ausentes", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<Page<EspacoResponse>> buscarDisponiveis(
            @Parameter(description = "Data de uso no formato ISO-8601 (yyyy-MM-dd)", example = "2026-05-10") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataUso,
            @Parameter(description = "Hora de início no formato ISO-8601 (HH:mm:ss)", example = "08:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @Parameter(description = "Hora de fim no formato ISO-8601 (HH:mm:ss)", example = "10:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFim,
            @ParameterObject Pageable filtros) {
        return ResponseEntity.ok(espacoService.buscarDisponiveis(dataUso, horaInicio, horaFim, filtros));
    }

    @GetMapping("/solicitacao/{id}")
    @Operation(operationId = "getEspacoSolicitacaoById", summary = "Buscar solicitação de reserva por ID", description = "Retorna detalhes de uma solicitação de reserva específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<SolicitacaoResponse> buscarSolicitacao(
            @Parameter(description = "ID da solicitação", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(espacoService.buscarSolicitacao(id));
    }

    @PutMapping("/solicitacao/{id}")
    @Operation(operationId = "updateEspacoSolicitacaoById", summary = "Atualizar solicitação de reserva", description = "Atualiza status e vínculos da solicitação de reserva para fluxo administrativo de aprovação/reprovação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação, professor ou espaço não encontrado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<SolicitacaoResponse> atualizarSolicitacao(
            @Parameter(description = "ID da solicitação", example = "1") @PathVariable Long id,
            @RequestBody @Valid UpdateSolicitacaoRequest request) {
        return ResponseEntity.ok(espacoService.atualizarSolicitacao(id, request));
    }

    @DeleteMapping("/solicitacao/{id}")
    @Operation(operationId = "deleteEspacoSolicitacaoById", summary = "Excluir solicitação de reserva", description = "Exclui uma solicitação de reserva por identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solicitação excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErroApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada", content = @Content(schema = @Schema(implementation = ErroApiResponse.class)))
    })
    public ResponseEntity<Void> deletarSolicitacao(
            @Parameter(description = "ID da solicitação", example = "1") @PathVariable Long id) {
        espacoService.deletarSolicitacao(id);
        return ResponseEntity.noContent().build();
    }
}