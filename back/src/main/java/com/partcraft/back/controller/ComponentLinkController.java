package com.partcraft.back.controller;

import com.partcraft.back.dto.ComponentLinkDTO;
import com.partcraft.back.service.ComponentLinkService;
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
@RequestMapping("api/component/link")
@Tag(name = "Component Links", description = "Endpoints for managing component purchase links and references")
@SecurityRequirement(name = "cookieAuth")
public class ComponentLinkController {

    @Autowired
    private ComponentLinkService componentLinkService;

    @Operation(
            summary = "Get all links for a component",
            description = "Retrieves all purchase/reference links associated with a specific component."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved component links",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentLinkDTO.class))),
            @ApiResponse(responseCode = "404", description = "Component not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ComponentLinkDTO>> getAllComponentLinks(
            @Parameter(description = "ID of the component", required = true, example = "1")
            @RequestParam Long componentId,
            @Parameter(description = "Type of the component (CPU, GPU, RAM, etc.)", required = true, example = "CPU")
            @RequestParam String componentType) {
        return ResponseEntity.ok().body(componentLinkService.getAllComponentLinks(componentId, componentType));
    }

    @Operation(
            summary = "Get component link by ID",
            description = "Retrieves a specific component link by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved component link",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentLinkDTO.class))),
            @ApiResponse(responseCode = "404", description = "Component link not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("{linkId}")
    public ResponseEntity<ComponentLinkDTO> getComponentLinkById(
            @Parameter(description = "ID of the component link", required = true, example = "1")
            @PathVariable Long linkId) {
        return ResponseEntity.ok().body(componentLinkService.getComponentLink(linkId));
    }

    @Operation(
            summary = "Create a new component link (Admin only)",
            description = "Creates a new purchase/reference link for a component. Only administrators can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Component link successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentLinkDTO.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid link data",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ComponentLinkDTO> createComponentLink(
            @Parameter(description = "Component link data", required = true)
            @RequestBody ComponentLinkDTO dto) {
        return ResponseEntity.ok(componentLinkService.createComponentLink(dto));
    }

    @Operation(
            summary = "Update component link (Admin only)",
            description = "Updates an existing component link. Only administrators can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Component link successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComponentLinkDTO.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Component link not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid link data",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{linkId}")
    public ResponseEntity<ComponentLinkDTO> updateComponentLink(
            @Parameter(description = "ID of the component link to update", required = true, example = "1")
            @PathVariable Long linkId,
            @Parameter(description = "Updated link data", required = true)
            @RequestBody ComponentLinkDTO dto) {
        return ResponseEntity.ok(componentLinkService.updateComponentLink(linkId, dto));
    }

    @Operation(
            summary = "Delete component link (Admin only)",
            description = "Deletes a component link. Only administrators can perform this action. This action is irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Component link successfully deleted"),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Component link not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteComponentLink(
            @Parameter(description = "ID of the component link to delete", required = true, example = "1")
            @PathVariable Long linkId) {
        componentLinkService.deleteComponentLink(linkId);
        return ResponseEntity.noContent().build();
    }
}
