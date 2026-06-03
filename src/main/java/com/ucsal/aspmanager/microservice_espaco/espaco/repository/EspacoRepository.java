package com.ucsal.aspmanager.microservice_espaco.espaco.repository;

import com.ucsal.aspmanager.microservice_espaco.espaco.model.Espaco;
import com.ucsal.aspmanager.microservice_espaco.shared.model.enums.StatusRegistro;
import com.ucsal.aspmanager.microservice_espaco.shared.model.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface EspacoRepository extends JpaRepository<Espaco, Long> {

    @Query("""
            SELECT e
            FROM Espaco e
            WHERE e.statusRegistro = :statusRegistro
              AND NOT EXISTS (
                SELECT s.id
                FROM SolicitacaoEspaco s
                WHERE s.espaco.id = e.id
                  AND s.statusSolicitacao = :statusSolicitacao
                  AND s.dataUso = :dataUso
                  AND s.horaInicio < :horaFim
                  AND s.horaFim > :horaInicio
              )
            """)
    Page<Espaco> buscarDisponiveisNoPeriodo(
            @Param("dataUso") LocalDate dataUso,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("statusRegistro") StatusRegistro statusRegistro,
            @Param("statusSolicitacao") StatusSolicitacao statusSolicitacao,
            Pageable pageable
    );

    @Query("SELECT e FROM Espaco e JOIN e.softwaresIds s WHERE s = :softwareId")
    List<Espaco> findBySoftwareId(@Param("softwareId") Long softwareId);
}