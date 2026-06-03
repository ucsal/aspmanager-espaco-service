package com.ucsal.aspmanager.microservice_espaco.espaco.repository;

import com.ucsal.aspmanager.microservice_espaco.espaco.model.SolicitacaoEspaco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoEspacoRepository extends JpaRepository<SolicitacaoEspaco, Long> {
    boolean existsByEspaco_Id(Long espacoId);

    Page<SolicitacaoEspaco> findByProfessorId(Long professorId, Pageable pageable);
}