package com.ucsal.aspmanager.microservice_espaco.espaco.mapper;

import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.CreateSolicitacaoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.response.SolicitacaoResponse;
import com.ucsal.aspmanager.microservice_espaco.espaco.model.SolicitacaoEspaco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SolicitacaoMapper {

    @Mapping(target = "espaco", ignore = true)
    @Mapping(target = "professorId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusSolicitacao", ignore = true)
    SolicitacaoEspaco toEntity(CreateSolicitacaoRequest request);

    @Mapping(source = "espaco.id", target = "idEspaco")
    @Mapping(source = "professorId", target = "idProfessor")
    SolicitacaoResponse toResponse(SolicitacaoEspaco entity);
}