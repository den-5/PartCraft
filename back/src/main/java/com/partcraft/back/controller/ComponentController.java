package com.partcraft.back.controller;

import com.partcraft.back.service.ComponentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class ComponentController<T, DTO, ID> {

    protected abstract ComponentService<T, DTO, ID> getService();

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DTO> create(@RequestBody DTO dto) {
        DTO created = getService().create(dto);
        return ResponseEntity.ok().body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DTO> update(@PathVariable ID id, @RequestBody DTO dto) {
        DTO updated = getService().update(id, dto);
        return ResponseEntity.ok().body(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<DTO> findById(@PathVariable ID id) {
        DTO dto = getService().findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<DTO>> findAll() {
        List<DTO> dtos = getService().findAll();
        return ResponseEntity.ok().body(dtos);
    }
}
