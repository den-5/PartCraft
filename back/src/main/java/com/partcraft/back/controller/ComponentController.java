package com.partcraft.back.controller;

import com.partcraft.back.service.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class ComponentController<T, DTO, ID> {

    protected abstract ComponentService<T, DTO, ID> getService();

    @Operation(
            summary = "Create a new component (Admin only)",
            description = "Creates a new hardware component. Only administrators can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Component successfully created",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid component data",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DTO> create(
            @Parameter(description = "Component data", required = true)
            @RequestBody DTO dto) {
        DTO created = getService().create(dto);
        return ResponseEntity.ok().body(created);
    }

    @Operation(
            summary = "Update component (Admin only)",
            description = "Updates an existing hardware component. Only administrators can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Component successfully updated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Component not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid component data",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DTO> update(
            @Parameter(description = "ID of the component to update", required = true)
            @PathVariable ID id,
            @Parameter(description = "Updated component data", required = true)
            @RequestBody DTO dto) {
        DTO updated = getService().update(id, dto);
        return ResponseEntity.ok().body(updated);
    }

    @Operation(
            summary = "Delete component (Admin only)",
            description = "Deletes a hardware component. Only administrators can perform this action. This action is irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Component successfully deleted"),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Component not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the component to delete", required = true)
            @PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get component by ID",
            description = "Retrieves detailed information about a specific hardware component."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved component",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Component not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<DTO> findById(
            @Parameter(description = "ID of the component", required = true)
            @PathVariable ID id) {
        DTO dto = getService().findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Get all components",
            description = "Retrieves a list of all hardware components of this type."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved components",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<DTO>> findAll() {
        List<DTO> dtos = getService().findAll();
        return ResponseEntity.ok().body(dtos);
    }
}
