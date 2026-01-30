package com.partcraft.back.service;

import com.partcraft.back.dto.PC.CreatePCDTO;
import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.PC.UpdatePCDTO;
import com.partcraft.back.dto.componentDTO.*;
import com.partcraft.back.entity.PC;
import com.partcraft.back.exception.service.ComponentCompatibilityServiceException;
import com.partcraft.back.exception.service.PCServiceException;
import com.partcraft.back.exception.NotFoundException;
import com.partcraft.back.repository.PCRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.service.helper.ComponentRepositoryManager;
import com.partcraft.back.service.helper.SetPCComponentsManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PCService {
    public final PCRepository pcRepository;
    public final UserService userService;
    public final UserRepository userRepository;
    public final ComponentRepositoryManager components;
    public final SetPCComponentsManager setPCComponentsManager;
    public final ComponentCompatibilityService compatibilityService;
    private final ModelMapper modelMapper;


    public PCService(PCRepository pcRepository, UserService userService,
                     UserRepository userRepository, ComponentRepositoryManager components,
                     SetPCComponentsManager setPCComponentsManager,
                     ComponentCompatibilityService compatibilityService, ModelMapper modelMapper) {
        this.pcRepository = pcRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.components = components;
        this.setPCComponentsManager = setPCComponentsManager;
        this.compatibilityService = compatibilityService;
        this.modelMapper = modelMapper;
    }

    public PCDTO createPC(CreatePCDTO createPCDTO, String username) throws PCServiceException {
        var owner = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));

        var pc = new PC();
        pc.setOwner(owner);
        pc.setName(createPCDTO.getName());
        pc.setDescription(createPCDTO.getDescription());
        pc.setPurpose(createPCDTO.getPurpose());
        pc.setLocation(createPCDTO.getLocation());
        pc.setVisibility(createPCDTO.getVisibility());
        setPCComponentsManager.setAllComponents(pc, createPCDTO);

        validateComponentCompatibility(pc);

        var savedPc = pcRepository.save(pc);
        return mapToDTO(savedPc);
    }

    public PCDTO updatePCFields(Long id, UpdatePCDTO updatePCDTO) {
        var pc = pcRepository.findById(id).orElseThrow(
                () -> new NotFoundException("PC with id " + id + " not found"));

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
                () -> new NotFoundException("PC with id " + id + " not found"));

        setPCComponentsManager.setAllComponents(pc, updatePCDTO);

        validateComponentCompatibility(pc);

        pcRepository.save(pc);
        return mapToDTO(pc);
    }

    public PCDTO getPCById(Long id) throws PCServiceException {
        var pc = pcRepository.findById(id).orElseThrow(
                () -> new NotFoundException("PC with id " + id + " not found"));

        return mapToDTO(pc);
    }

    public List<PCDTO> getAllUserPCs(String username) throws PCServiceException {
        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));

        List<PC> userPCs = pcRepository.findAllByOwnerId(user.getId()).orElseThrow(
                () -> new NotFoundException("no PCs found by username: " + username)
        );

        return userPCs.stream().map(this::mapToDTO).toList();

    }

    public void deletePCbyId(Long Id) throws PCServiceException {
        var pc = pcRepository.findById(Id).orElseThrow(
                () -> new NotFoundException("PC with id " + Id + " not found")
        );
        pcRepository.delete(pc);
    }


    private PCDTO mapToDTO(PC pc) {
        PCDTO pcDTO = modelMapper.map(pc, PCDTO.class);
        pcDTO.setOwnerId(pc.getOwner().getId());
        return pcDTO;
    }

    private void validateComponentCompatibility(PC pc) {
        try {
            if (pc.getCpu() != null && pc.getMotherboard() != null) {
                compatibilityService.isCpuAndMotherboardCompatible(new CPUDTO(pc.getCpu()), new MotherBoardDTO(pc.getMotherboard()));
            }
            if (pc.getMotherboard() != null && pc.getRamKit() != null) {
                compatibilityService.isMotherboardAndRAMCompatible(new MotherBoardDTO(pc.getMotherboard()), new RAMKitDTO(pc.getRamKit()));
            }
            if (pc.getGpu() != null && pc.getPcCase() != null) {
                compatibilityService.isGPUAndCaseCompatible(new GPUDTO(pc.getGpu()), new CaseDTO(pc.getPcCase()));
            }
            if (pc.getCpuCooler() != null) {
                compatibilityService.isCPUCoolerCompatible(mapToDTO(pc), new CPUCoolerDTO(pc.getCpuCooler()));
            }
            if (pc.getPsu() != null) {
                compatibilityService.isPSUCompatible(mapToDTO(pc), new PSUDTO(pc.getPsu()));
            }
        } catch (ComponentCompatibilityServiceException e) {
            throw new PCServiceException("Component compatibility error: " + e.getMessage());
        }
    }
}
