package com.ucsal.aspmanager.microservice_espaco.shared.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceBase<ID, CREATE, UPDATE, RESPONSE> {
    RESPONSE criar(CREATE request);
    Page<RESPONSE> buscarTodos(Pageable filtros);
    RESPONSE buscar(ID id);
    RESPONSE atualizar(ID id, UPDATE request);
    void deletar(ID id);
}