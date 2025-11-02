package com.partcraft.back.service;

import com.partcraft.back.dto.PC.CreatePCDTO;
import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.componentDTO.*;
import com.partcraft.back.entity.PC;
import com.partcraft.back.entity.component.*;
import com.partcraft.back.exception.PCServiceException;
import com.partcraft.back.repository.component.PCRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.service.helper.ComponentRepositoryManager;

import java.util.ArrayList;
import java.util.List;

public class PCService {
    public final PCRepository pcRepository;
    public final UserService userService;
    public final UserRepository userRepository;
    public final ComponentRepositoryManager components;


    public PCService(PCRepository pcRepository, UserService userService,
                     UserRepository userRepository, ComponentRepositoryManager components) {
        this.pcRepository = pcRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.components = components;
    }

    public PCDTO createPC(CreatePCDTO createPCDTO, String username) throws PCServiceException {
        var owner = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new PCServiceException("User with username " + username + " not found"));

        var pc = new PC();
        pc.setOwner(owner);
        pc.setName(createPCDTO.getName());
        pc.setDescription(createPCDTO.getDescription());
        pc.setPurpose(createPCDTO.getPurpose());
        pc.setLocation(createPCDTO.getLocation());
        pc.setVisibility(createPCDTO.getVisibility());

        if (createPCDTO.getCpuId() != null) {
            var cpu = components.getCpuRepository().findById(createPCDTO.getCpuId())
                    .orElseThrow(() -> new PCServiceException("CPU with id " + createPCDTO.getCpuId() + " not found"));
            pc.setCpu(cpu);
        }

        if (createPCDTO.getGpuId() != null) {
            var gpu = components.getGpuRepository().findById(createPCDTO.getGpuId())
                    .orElseThrow(() -> new PCServiceException("GPU with id " + createPCDTO.getGpuId() + " not found"));
            pc.setGpu(gpu);
        }

        if (createPCDTO.getRamKitId() != null) {
            var ramKit = components.getRamKitRepository().findById(createPCDTO.getRamKitId())
                    .orElseThrow(() -> new PCServiceException("RAM Kit with id " + createPCDTO.getRamKitId() + " not found"));
            pc.setRamKit(ramKit);
        }

        if (createPCDTO.getStorageId() != null) {
            var storage = components.getStorageRepository().findById(createPCDTO.getStorageId())
                    .orElseThrow(() -> new PCServiceException("Storage with id " + createPCDTO.getStorageId() + " not found"));
            pc.setStorage(storage);
        }

        if (createPCDTO.getPsuId() != null) {
            var psu = components.getPsuRepository().findById(createPCDTO.getPsuId())
                    .orElseThrow(() -> new PCServiceException("PSU with id " + createPCDTO.getPsuId() + " not found"));
            pc.setPsu(psu);
        }

        if (createPCDTO.getCpuCoolerId() != null) {
            var cpuCooler = components.getCpuCoolerRepository().findById(createPCDTO.getCpuCoolerId())
                    .orElseThrow(() -> new PCServiceException("CPU Cooler with id " + createPCDTO.getCpuCoolerId() + " not found"));
            pc.setCpuCooler(cpuCooler);
        }

        if (createPCDTO.getMotherboardId() != null) {
            var motherboard = components.getMotherBoardRepository().findById(createPCDTO.getMotherboardId())
                    .orElseThrow(() -> new PCServiceException("Motherboard with id " + createPCDTO.getMotherboardId() + " not found"));
            pc.setMotherboard(motherboard);
        }

        if (createPCDTO.getPcCaseId() != null) {
            var pcCase = components.getCaseRepository().findById(createPCDTO.getPcCaseId())
                    .orElseThrow(() -> new PCServiceException("Case with id " + createPCDTO.getPcCaseId() + " not found"));
            pc.setPcCase(pcCase);
        }

        if (createPCDTO.getCoolerIds() != null && !createPCDTO.getCoolerIds().isEmpty()) {
            List<CaseCooler> caseCoolers = new ArrayList<>();
            for (var coolerId : createPCDTO.getCoolerIds()) {
                var caseCooler = components.getCaseCoolerRepository().findById(coolerId)
                        .orElseThrow(() -> new PCServiceException("Cooler with id " + coolerId + " not found"));
                caseCoolers.add(caseCooler);
            }
            pc.setCoolers(caseCoolers);
        }

        var savedPc = pcRepository.save(pc);
        return mapToDTO(savedPc);
    }

    private PCDTO mapToDTO(PC pc) {
        var dto = new PCDTO();
        dto.setId(pc.getId());
        dto.setOwnerId(pc.getOwner().getId());
        dto.setName(pc.getName());
        dto.setDescription(pc.getDescription());
        dto.setPurpose(pc.getPurpose());
        dto.setCpu(pc.getCpu() != null ? new CPUDTO(pc.getCpu()) : null);
        dto.setGpu(pc.getGpu() != null ? new GPUDTO(pc.getGpu()) : null);
        dto.setRamKit(pc.getRamKit() != null ? new RAMKitDTO(pc.getRamKit()) : null);
        dto.setStorage(pc.getStorage() != null ? new StorageDTO(pc.getStorage()) : null);
        dto.setPsu(pc.getPsu() != null ? new PSUDTO(pc.getPsu()) : null);
        dto.setCpuCooler(pc.getCpuCooler() != null ? new CPUCoolerDTO(pc.getCpuCooler()) : null);
        dto.setMotherboard(pc.getMotherboard() != null ? new MotherBoardDTO(pc.getMotherboard()) : null);
        dto.setPcCase(pc.getPcCase() != null ? new CaseDTO(pc.getPcCase()) : null);
        dto.setCoolers(pc.getCoolers() != null ? pc.getCoolers().stream().map(CaseCoolerDTO::new).toList() : null);
        dto.setBenchmarkScore(pc.getBenchmarkScore());
        dto.setTemperatureIdleC(pc.getTemperatureIdleC());
        dto.setTemperatureLoadC(pc.getTemperatureLoadC());
        dto.setNoiseLevelDb(pc.getNoiseLevelDb());
        dto.setEstimatedValueUsd(pc.getEstimatedValueUsd());
        dto.setTotalPowerDrawW(pc.getTotalPowerDrawW());
        dto.setCreatedAt(pc.getCreatedAt());
        dto.setUpdatedAt(pc.getUpdatedAt());
        dto.setLocation(pc.getLocation());
        dto.setVisibility(pc.getVisibility());
        dto.setTags(pc.getTags());
        return dto;
    }
}
