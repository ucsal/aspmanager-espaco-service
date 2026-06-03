package com.ucsal.aspmanager.microservice_espaco.espaco.mapper;

import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.CreateEspacoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.request.UpdateEspacoRequest;
import com.ucsal.aspmanager.microservice_espaco.espaco.dto.response.EspacoResponse;
import com.ucsal.aspmanager.microservice_espaco.espaco.model.Espaco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EspacoMapper {

    @Mapping(target = "idEscola", ignore = true)
    @Mapping(target = "softwaresIds", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusRegistro", ignore = true)
    Espaco toEntity(CreateEspacoRequest request);

    @Mapping(source = "softwaresIds", target = "softwares")
    EspacoResponse toResponse(Espaco entity);

    @Mapping(target = "idEscola", ignore = true)
    @Mapping(target = "softwaresIds", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusRegistro", ignore = true)
    void updateEntity(UpdateEspacoRequest request, @MappingTarget Espaco entity);
}