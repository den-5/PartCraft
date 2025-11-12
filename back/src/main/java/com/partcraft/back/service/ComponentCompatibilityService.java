package com.partcraft.back.service;

import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.componentDTO.*;
import com.partcraft.back.enums.CoolingType;
import com.partcraft.back.exception.ComponentCompatibilityServiceException;
import com.partcraft.back.repository.component.ComponentPlacementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class ComponentCompatibilityService {

    @Autowired
    public ComponentPlacementRepository componentRepository;

    public boolean isCpuAndMotherboardCompatible(CPUDTO cpu, MotherBoardDTO motherboard) {
        if (motherboard.getSocketType() == null || cpu.getSocketType() == null) {
            throw new ComponentCompatibilityServiceException("CPU or motherboard socket type is null");
        }
        if (!Objects.equals(motherboard.getSocketType(), cpu.getSocketType())) {
            throw new ComponentCompatibilityServiceException("CPU and motherboard sockets do not match");
        }
        return true;
    }

    public boolean isMotherboardAndRAMCompatible(MotherBoardDTO motherboard, RAMKitDTO ramKit) {
        if (motherboard.getMemoryType() == null || ramKit.getRamType() == null) {
            throw new ComponentCompatibilityServiceException("Motherboard or RAM memory type is null");
        }
        if (!Objects.equals(motherboard.getMemoryType(), ramKit.getRamType())) {
            throw new ComponentCompatibilityServiceException("Motherboard and RAM memory types do not match");
        }
        return true;
    }

    public boolean isGPUAndCaseCompatible(GPUDTO gpu, CaseDTO pcCase) {
        var gpuSize = gpu.getSize();
        var gpuMaxSize = pcCase.getComponentPlacementIds().stream().map(id -> componentRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(component -> Objects.equals(component.getComponentType(), "GPU"))
                .findFirst()
                .orElseThrow(() -> new ComponentCompatibilityServiceException("GPU component placement not found in case"))
                .getMaxSize();
        return gpuSize.getHeight() <= gpuMaxSize.getHeight() &&
                gpuSize.getWidth() <= gpuMaxSize.getWidth() &&
                gpuSize.getLength() <= gpuMaxSize.getLength();
    }

    public Optional<List<CaseCoolerDTO>> checkCaseCoolersAndCaseCompatibility(CaseCoolerDTO[] caseCoolers, CaseDTO pcCase, CPUCoolerDTO cpuCooler) {
        var places = pcCase.getComponentPlacementIds().stream().map(id -> componentRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(component -> Objects.equals(component.getComponentType(), "CaseCooler"))
                .toList();

        List<CaseCoolerDTO> coolersLeft = new ArrayList<>(List.of(caseCoolers));
        for (var place : places) {
            for (int j = 0; j < coolersLeft.size(); j++) {
                if (place.getMaxSize().getHeight() >= coolersLeft.get(j).getSize().getHeight()) {
                    coolersLeft.remove(j);
                    break;
                }
            }
        }
        return coolersLeft.isEmpty() ? Optional.empty() : Optional.of(coolersLeft);
    }

    public boolean isCPUCoolerCompatible(PCDTO pc, CPUCoolerDTO cpuCooler) {
        if (cpuCooler.getCoolingType() == null) {
            throw new ComponentCompatibilityServiceException("CPU cooler cooling type is null");
        }

        if (cpuCooler.getCoolingType() == CoolingType.Air) {
            var airCoolerPlace = pc.getPcCase().getComponentPlacementIds().stream()
                    .map(id -> componentRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .filter(component -> Objects.equals(component.getComponentType(), "CPUCooler"))
                    .findFirst();

            if (airCoolerPlace.isEmpty()) {
                throw new ComponentCompatibilityServiceException("CPU cooler placement not found in case");
            }

            boolean doesCPUCoolerTowerFit = cpuCooler.getSize().getHeight() <= airCoolerPlace.get().getMaxSize().getHeight()
                    && cpuCooler.getSize().getWidth() <= airCoolerPlace.get().getMaxSize().getWidth()
                    && cpuCooler.getSize().getLength() <= airCoolerPlace.get().getMaxSize().getLength();

            if (!doesCPUCoolerTowerFit) {
                throw new ComponentCompatibilityServiceException("CPU cooler does not fit in the case");
            }

            boolean isCoolerMaxTDPHigherThanCPUMaxTDP = cpuCooler.getMaxTDP() >= pc.getCpu().getPowerDraw();

            if (!isCoolerMaxTDPHigherThanCPUMaxTDP) {
                throw new ComponentCompatibilityServiceException("CPU cooler TDP (" + cpuCooler.getMaxTDP() + "W) is insufficient for CPU power draw (" + pc.getCpu().getPowerDraw() + "W)");
            }

            boolean isCoolerAndMotherBoardSocketCompatible = cpuCooler.getCpuSocket().equals(pc.getCpu().getSocketType());

            if (!isCoolerAndMotherBoardSocketCompatible) {
                throw new ComponentCompatibilityServiceException("CPU cooler socket (" + cpuCooler.getCpuSocket() + ") does not match CPU socket (" + pc.getCpu().getSocketType() + ")");
            }

            return true;
        }
        // Liquid cooler logic
        var slotsInPCCase = pc.getPcCase().getComponentPlacementIds().stream()
                .map(id -> componentRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(component -> Objects.equals(component.getComponentType(), "CaseCooler"))
                .filter(component -> component.getMaxSize().getWidth() >= cpuCooler.getSize().getWidth())
                .filter(component -> component.getMaxSize().getHeight() >= cpuCooler.getSize().getHeight())
                .toList();

        //x 0 == right x -1 == left, y 0 == bottom y -1 == top;
        Long maxAvailableSlotsInTheRow = Math.max(
                Math.max(slotsInPCCase.stream().filter(c -> c.getX() == 0).count(),
                        slotsInPCCase.stream().filter(c -> c.getY() == 0).count()),
                Math.max(slotsInPCCase.stream().filter(c -> c.getX() == -1).count(),
                        slotsInPCCase.stream().filter(c -> c.getY() == -1).count())
        );

        boolean isEnoughSlotsInCase = cpuCooler.getCaseCoolerSlotsRequired() <= maxAvailableSlotsInTheRow;

        if (!isEnoughSlotsInCase) {
            throw new ComponentCompatibilityServiceException("Not enough case cooler slots for liquid CPU cooler radiator. Required: " + cpuCooler.getCaseCoolerSlotsRequired() + ", available: " + maxAvailableSlotsInTheRow);
        }

        boolean isEnoughSlotsLeft = false;

        // Logic to check if there is enough space left for case coolers
        var places = new ArrayList<>(pc.getPcCase().getComponentPlacementIds().stream()
                .map(id -> componentRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(component -> Objects.equals(component.getComponentType(), "CaseCooler"))
                .toList());

        int removedElements = 0;
        int i = 0;
        while (removedElements < cpuCooler.getCaseCoolerSlotsRequired() && i < places.size()) {
            if (places.get(i).getMaxSize().getHeight() >= cpuCooler.getSize().getHeight()) {
                places.remove(i);
                removedElements++;
            } else {
                i++;
            }
        }

        List<CaseCoolerDTO> coolersLeft = new ArrayList<>(pc.getCoolers());
        for (var place : places) {
            for (int j = 0; j < coolersLeft.size(); j++) {
                if (place.getMaxSize().getHeight() >= pc.getCoolers().get(j).getSize().getHeight()) {
                    coolersLeft.remove(pc.getCoolers().get(j));
                    break;
                }
            }
        }
        isEnoughSlotsLeft = coolersLeft.isEmpty();

        if (!isEnoughSlotsLeft) {
            throw new ComponentCompatibilityServiceException("Not enough space for existing case coolers after installing liquid CPU cooler radiator. Remaining coolers: " + coolersLeft.size());
        }

        if (!pc.getMotherboard().getSocketType().equals(cpuCooler.getCpuSocket())) {
            throw new ComponentCompatibilityServiceException("CPU cooler socket (" + cpuCooler.getCpuSocket() + ") does not match motherboard socket (" + pc.getMotherboard().getSocketType() + ")");
        }

        if (pc.getCpu().getPowerDraw() > cpuCooler.getMaxTDP()) {
            throw new ComponentCompatibilityServiceException("CPU cooler TDP (" + cpuCooler.getMaxTDP() + "W) is insufficient for CPU power draw (" + pc.getCpu().getPowerDraw() + "W)");
        }

        return true;
    }

    public boolean isPSUCompatible() {
        return true;
    }

    public void setComponentRepository(ComponentPlacementRepository repository) {
        this.componentRepository = repository;
    }
}
