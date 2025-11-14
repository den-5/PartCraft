package com.partcraft.back.controller;

import com.partcraft.back.dto.ComponentLinkDTO;
import com.partcraft.back.service.ComponentLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/component/link")
public class ComponentLinkController {

    @Autowired
    private ComponentLinkService componentLinkService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ComponentLinkDTO>> getAllComponentLinks(@RequestParam Long componentId, @RequestParam String componentType) {
        return ResponseEntity.ok().body(componentLinkService.getAllComponentLinks(componentId, componentType));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("{linkId}")
    public ResponseEntity<ComponentLinkDTO> getComponentLinkById(@PathVariable Long linkId) {
        return ResponseEntity.ok().body(componentLinkService.getComponentLink(linkId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ComponentLinkDTO> createComponentLink(@RequestBody ComponentLinkDTO dto) {
        return ResponseEntity.ok(componentLinkService.createComponentLink(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{linkId}")
    public ResponseEntity<ComponentLinkDTO> updateComponentLink(@PathVariable Long linkId, @RequestBody ComponentLinkDTO dto) {
        return ResponseEntity.ok(componentLinkService.updateComponentLink(linkId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteComponentLink(@PathVariable Long linkId) {
        componentLinkService.deleteComponentLink(linkId);
        return ResponseEntity.noContent().build();
    }
}
