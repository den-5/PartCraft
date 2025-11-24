package com.partcraft.back.controller;

import com.partcraft.back.dto.ComponentPriceDTO;
import com.partcraft.back.service.ComponentPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/component/price")
@Tag(name = "Component Prices", description = "Endpoints for managing component pricing history and current prices")
@SecurityRequirement(name = "cookieAuth")
public class ComponentPriceController {

    @Autowired
    private ComponentPriceService componentPriceService;

    @Operation(
            summary = "Get price history for a component",
            description = "Retrieves the complete price history for a specific component, useful for tracking price trends."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved price history",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentPriceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Component not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/history")
    public ResponseEntity<List<ComponentPriceDTO>> getAllComponentPrices(
            @Parameter(description = "ID of the component", required = true, example = "1")
            @RequestParam Long componentId,
            @Parameter(description = "Type of the component (CPU, GPU, RAM, etc.)", required = true, example = "CPU")
            @RequestParam String componentType) {
        return ResponseEntity.ok().body(componentPriceService.getAllComponentPrices(componentId, componentType));
    }

    @Operation(
            summary = "Get component price by ID",
            description = "Retrieves a specific price record by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved price record",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentPriceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Price record not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{priceId}")
    public ResponseEntity<ComponentPriceDTO> getComponentPriceById(
            @Parameter(description = "ID of the price record", required = true, example = "1")
            @PathVariable Long priceId) {
        return ResponseEntity.ok().body(componentPriceService.getComponentPrice(priceId));
    }

    @Operation(
            summary = "Create a new price record (Admin only)",
            description = "Creates a new price record for a component. Only administrators can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price record successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentPriceDTO.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid price data",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ComponentPriceDTO> createComponentPrice(
            @Parameter(description = "Price record data", required = true)
            @RequestBody ComponentPriceDTO dto) {
        return ResponseEntity.ok(componentPriceService.createComponentPrice(dto));
    }

    @Operation(
            summary = "Update price record (Admin only)",
            description = "Updates an existing price record. Only administrators can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price record successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentPriceDTO.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Price record not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid price data",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{priceId}")
    public ResponseEntity<ComponentPriceDTO> updateComponentPrice(
            @Parameter(description = "ID of the price record to update", required = true, example = "1")
            @PathVariable Long priceId,
            @Parameter(description = "Updated price data", required = true)
            @RequestBody ComponentPriceDTO dto) {
        return ResponseEntity.ok(componentPriceService.updateComponentPrice(priceId, dto));
    }

    @Operation(
            summary = "Delete price record (Admin only)",
            description = "Deletes a price record. Only administrators can perform this action. This action is irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Price record successfully deleted"),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Price record not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{priceId}")
    public ResponseEntity<Void> deleteComponentPrice(
            @Parameter(description = "ID of the price record to delete", required = true, example = "1")
            @PathVariable Long priceId) {
        componentPriceService.deleteComponentPrice(priceId);
        return ResponseEntity.noContent().build();
    }
}

