package com.partcraft.back.controller;

import com.partcraft.back.dto.ComponentPriceDTO;
import com.partcraft.back.service.ComponentPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/component/price")
public class ComponentPriceController {

    @Autowired
    private ComponentPriceService componentPriceService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/history")
    public ResponseEntity<List<ComponentPriceDTO>> getAllComponentPrices(@RequestParam Long componentId, @RequestParam String componentType) {
        return ResponseEntity.ok().body(componentPriceService.getAllComponentPrices(componentId, componentType));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{priceId}")
    public ResponseEntity<ComponentPriceDTO> getComponentPriceById(@PathVariable Long priceId) {
        return ResponseEntity.ok().body(componentPriceService.getComponentPrice(priceId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ComponentPriceDTO> createComponentPrice(@RequestBody ComponentPriceDTO dto) {
        return ResponseEntity.ok(componentPriceService.createComponentPrice(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{priceId}")
    public ResponseEntity<ComponentPriceDTO> updateComponentPrice(@PathVariable Long priceId, @RequestBody ComponentPriceDTO dto) {
        return ResponseEntity.ok(componentPriceService.updateComponentPrice(priceId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{priceId}")
    public ResponseEntity<Void> deleteComponentPrice(@PathVariable Long priceId) {
        componentPriceService.deleteComponentPrice(priceId);
        return ResponseEntity.noContent().build();
    }
}

