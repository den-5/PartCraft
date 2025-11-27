package com.partcraft.back.controller;

import com.partcraft.back.dto.PC.CreatePCDTO;
import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.PC.UpdatePCDTO;
import com.partcraft.back.service.PCService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/api/pc"))
@Tag(name = "PC Management", description = "Endpoints for managing PC configurations and builds")
@SecurityRequirement(name = "cookieAuth")
public class PCController {
    private final PCService pcService;

    public PCController(PCService pcService) {
        this.pcService = pcService;
    }

    @Operation(
            summary = "Create a new PC configuration",
            description = "Creates a new PC build configuration for the authenticated user with selected components."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PC successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PCDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid PC configuration data or PC service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/")
    public ResponseEntity<PCDTO> createPC(
            @Parameter(description = "PC configuration data", required = true)
            @RequestBody CreatePCDTO createPCDTO) {
        var pcDTO = pcService.createPC(createPCDTO, SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(pcDTO);
    }

    @Operation(
            summary = "Get PC configuration by ID",
            description = "Retrieves detailed information about a specific PC configuration by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved PC configuration",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PCDTO.class))),
            @ApiResponse(responseCode = "400", description = "PC service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "PC configuration not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PCDTO> getPCById(
            @Parameter(description = "ID of the PC configuration", required = true, example = "1")
            @PathVariable Long id) {
        var pcDTO = pcService.getPCById(id);
        return ResponseEntity.ok(pcDTO);
    }

    @Operation(
            summary = "Get all PC configurations for a user",
            description = "Retrieves all PC configurations created by a specific user.\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's PC configurations",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PCDTO.class))),
            @ApiResponse(responseCode = "400", description = "PC service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @GetMapping("/user/{username}")
    public ResponseEntity<List<PCDTO>> getUserPcs(
            @Parameter(description = "Username of the PC owner (5-20 alphanumeric characters)", required = true, example = "john_doe")
            @PathVariable String username) {
        var pcDTO = pcService.getAllUserPCs(username);
        return ResponseEntity.ok(pcDTO);
    }

    @Operation(
            summary = "Update PC configuration fields",
            description = "Updates basic fields of a PC configuration (name, description, visibility, etc.) without modifying components."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PC fields successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PCDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data or PC service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "PC configuration not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @PutMapping("/update-fields/{id}")
    public ResponseEntity<PCDTO> updatePCFields(
            @Parameter(description = "ID of the PC configuration to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Fields to update", required = true)
            @RequestBody UpdatePCDTO updateDTO) {
        return ResponseEntity.ok(pcService.updatePCFields(id, updateDTO));
    }

    @Operation(
            summary = "Update PC components",
            description = "Updates the hardware components in a PC configuration (CPU, GPU, RAM, etc.)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PC components successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PCDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid component configuration or PC service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "PC configuration not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @PutMapping("/update-components/{id}")
    public ResponseEntity<PCDTO> updatePCComponents(
            @Parameter(description = "ID of the PC configuration to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Component updates", required = true)
            @RequestBody UpdatePCDTO updateDTO) {
        return ResponseEntity.ok(pcService.updatePCComponents(id, updateDTO));
    }

    @Operation(
            summary = "Delete PC configuration",
            description = "Deletes a PC configuration by its ID. This action is irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PC configuration successfully deleted"),
            @ApiResponse(responseCode = "400", description = "PC service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "PC configuration not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePCById(
            @Parameter(description = "ID of the PC configuration to delete", required = true, example = "1")
            @PathVariable Long id) {
        pcService.deletePCbyId(id);
        return ResponseEntity.ok().build();
    }
}
