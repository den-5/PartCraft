package com.partcraft.back.service;

import com.partcraft.back.dto.PC.CreatePCDTO;
import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.PC.UpdatePCDTO;
import com.partcraft.back.dto.componentDTO.*;
import com.partcraft.back.entity.PC;
import com.partcraft.back.exception.PCServiceException;
import com.partcraft.back.repository.PCRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.service.helper.ComponentRepositoryManager;
import com.partcraft.back.service.helper.SetPCComponentsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PCService {
    public final PCRepository pcRepository;
    public final UserService userService;
    public final UserRepository userRepository;
    public final ComponentRepositoryManager components;
    public final SetPCComponentsManager setPCComponentsManager;


    public PCService(PCRepository pcRepository, UserService userService,
                     UserRepository userRepository, ComponentRepositoryManager components,
                     SetPCComponentsManager setPCComponentsManager) {
        this.pcRepository = pcRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.components = components;
        this.setPCComponentsManager = setPCComponentsManager;
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
        setPCComponentsManager.setAllComponents(pc, createPCDTO);

        var savedPc = pcRepository.save(pc);
        return mapToDTO(savedPc);
    }

    public PCDTO updatePCFields(Long id, UpdatePCDTO updatePCDTO) {
        var pc = pcRepository.findById(id).orElseThrow(
                () -> new PCServiceException("PC with id " + id + " not found"));

        pc.setName(updatePCDTO.getName());
        pc.setDescription(updatePCDTO.getDescription());
        pc.setPurpose(updatePCDTO.getPurpose());
        pc.setVisibility(updatePCDTO.getVisibility());
        pc.setTags(updatePCDTO.getTags());
        pcRepository.save(pc);

        return mapToDTO(pc);
    }

    public PCDTO updatePCComponents(Long id, UpdatePCDTO updatePCDTO) {
        var pc = pcRepository.findById(id).orElseThrow(
                () -> new PCServiceException("PC with id " + id + " not found"));

        setPCComponentsManager.setAllComponents(pc, updatePCDTO);

        pcRepository.save(pc);
        return mapToDTO(pc);
    }

    public PCDTO getPCById(Long id) throws PCServiceException {
        var pc = pcRepository.findById(id).orElseThrow(
                () -> new PCServiceException("PC with id " + id + " not found"));

        return mapToDTO(pc);
    }

    public List<PCDTO> getAllUserPCs(String username) throws PCServiceException {
        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new PCServiceException("User with username " + username + " not found"));

        List<PC> userPCs = pcRepository.findAllByOwnerId(user.getId()).orElseThrow(
                () -> new PCServiceException("no PCs found by username: " + username)
        );

        return userPCs.stream().map(this::mapToDTO).toList();

    }

    public void deletePCbyId(Long Id) throws PCServiceException {
        var pc = pcRepository.findById(Id).orElseThrow(
                () -> new PCServiceException("PC with id " + Id + " not found")
        );
        pcRepository.delete(pc);
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
