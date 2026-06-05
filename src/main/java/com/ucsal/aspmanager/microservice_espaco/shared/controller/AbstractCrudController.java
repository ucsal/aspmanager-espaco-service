package com.ucsal.aspmanager.microservice_espaco.shared.controller;

import com.ucsal.aspmanager.microservice_espaco.shared.service.ServiceBase;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public abstract class AbstractCrudController<ID, CREATE, UPDATE, RESPONSE> {

    private final ServiceBase<ID, CREATE, UPDATE, RESPONSE> service;

    protected AbstractCrudController(ServiceBase<ID, CREATE, UPDATE, RESPONSE> service) {
        this.service = service;
    }

    protected abstract URI location(RESPONSE response, UriComponentsBuilder uriBuilder);

    @PostMapping
    public ResponseEntity<RESPONSE> criar(@RequestBody @Valid CREATE request, UriComponentsBuilder uriBuilder) {
        RESPONSE response = service.criar(request);
        return ResponseEntity.created(location(response, uriBuilder)).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<RESPONSE>> buscarTodos(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RESPONSE> buscar(@PathVariable ID id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RESPONSE> atualizar(@PathVariable ID id, @RequestBody @Valid UPDATE request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable ID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}