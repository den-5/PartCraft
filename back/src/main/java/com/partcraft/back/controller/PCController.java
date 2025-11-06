package com.partcraft.back.controller;

import com.partcraft.back.dto.PC.CreatePCDTO;
import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.PC.UpdatePCDTO;
import com.partcraft.back.service.PCService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/api/pc"))
public class PCController {
    private final PCService pcService;

    public PCController(PCService pcService) {
        this.pcService = pcService;
    }

    @PostMapping("/")
    public ResponseEntity<PCDTO> createPC(@RequestBody CreatePCDTO createPCDTO) {
        var pcDTO = pcService.createPC(createPCDTO, SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(pcDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PCDTO> getPCById(@PathVariable Long id) {
        var pcDTO = pcService.getPCById(id);
        return ResponseEntity.ok(pcDTO);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PCDTO>> getUserPcs(@PathVariable String username) {
        var pcDTO = pcService.getAllUserPCs(username);
        return ResponseEntity.ok(pcDTO);
    }

    @PutMapping("/update-fields/{id}")
    public ResponseEntity<PCDTO> updatePCFields(@PathVariable Long id, @RequestBody UpdatePCDTO updateDTO) {
        return ResponseEntity.ok(pcService.updatePCFields(id, updateDTO));
    }

    @PutMapping("/update-components/{id}")
    public ResponseEntity<PCDTO> updatePCComponents(@PathVariable Long id, @RequestBody UpdatePCDTO updateDTO) {
        return ResponseEntity.ok(pcService.updatePCComponents(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePCById(@PathVariable Long id) {
        pcService.deletePCbyId(id);
        return ResponseEntity.ok().build();
    }
}
