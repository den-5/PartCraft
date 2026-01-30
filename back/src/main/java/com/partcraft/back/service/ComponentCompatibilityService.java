package com.partcraft.back.service;

import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.componentDTO.*;
import com.partcraft.back.enums.ComponentType;
import com.partcraft.back.enums.CoolingType;
import com.partcraft.back.exception.service.ComponentCompatibilityServiceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ComponentCompatibilityService {

    public boolean isCpuAndMotherboardCompatible(CPUDTO cpu, MotherBoardDTO motherboard) {

        if (motherboard.getSocketType() == null || cpu.getSocketType() == null) {
            throw new ComponentCompatibilityServiceException("CPU or motherboard socket type is null");
        }
        if (!motherboard.getSocketType().equals(cpu.getSocketType())) {
            throw new ComponentCompatibilityServiceException("CPU and motherboard sockets do not match");
        }
        return true;
    }

    public boolean isMotherboardAndRAMCompatible(MotherBoardDTO motherboard, RAMKitDTO ramKit) {

        if (motherboard.getMemoryType() == null || ramKit.getRamType() == null) {
            throw new ComponentCompatibilityServiceException("Motherboard or RAM memory type is null");
        }
        if (!motherboard.getMemoryType().equals(ramKit.getRamType())) {
            throw new ComponentCompatibilityServiceException("Motherboard and RAM memory types do not match");
        }
        return true;
    }

    public boolean isGPUAndCaseCompatible(GPUDTO gpu, CaseDTO pcCase) {

        var gpuSize = gpu.getSize();
        var gpuPlacement = pcCase.getComponentPlacements().stream()
                .filter(component -> component.getComponentType() == ComponentType.GPU)
                .findFirst()
                .orElseThrow(() -> new ComponentCompatibilityServiceException("GPU component placement not found in case"));
        var gpuMaxSize = gpuPlacement.getMaxSize();
        return gpuSize.getHeight() <= gpuMaxSize.getHeight() &&
                gpuSize.getWidth() <= gpuMaxSize.getWidth() &&
                gpuSize.getLength() <= gpuMaxSize.getLength();
    }

    public Optional<List<CaseCoolerDTO>> checkCaseCoolersAndCaseCompatibility(CaseCoolerDTO[] caseCoolers, CaseDTO pcCase, CPUCoolerDTO cpuCooler) {

        var places = pcCase.getComponentPlacements().stream()
                .filter(component -> component.getComponentType() == ComponentType.CaseCooler)
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
            var airCoolerPlace = pc.getPcCase().getComponentPlacements().stream()
                    .filter(component -> component.getComponentType() == ComponentType.CPUCooler)
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


            if (cpuCooler.getMaxTDP() < pc.getCpu().getPowerDraw()) {
                throw new ComponentCompatibilityServiceException("CPU cooler TDP (" + cpuCooler.getMaxTDP() + "W) is insufficient for CPU power draw (" + pc.getCpu().getPowerDraw() + "W)");
            }
            if (!cpuCooler.getCpuSocket().equals(pc.getCpu().getSocketType())) {
                throw new ComponentCompatibilityServiceException("CPU cooler socket (" + cpuCooler.getCpuSocket() + ") does not match CPU socket (" + pc.getCpu().getSocketType() + ")");
            }

            return true;
        }



        var slotsInPCCase = pc.getPcCase().getComponentPlacements().stream()
                .filter(component -> component.getComponentType() == ComponentType.CaseCooler)
                .filter(component -> component.getMaxSize().getWidth() >= cpuCooler.getSize().getWidth())
                .filter(component -> component.getMaxSize().getHeight() >= cpuCooler.getSize().getHeight())
                .toList();


        int slotsToConsume = (cpuCooler.getCaseCoolerSlotsRequired() != null && cpuCooler.getCaseCoolerSlotsRequired() > 0)
                ? cpuCooler.getCaseCoolerSlotsRequired()
                : cpuCooler.getFanCount();


        long maxAvailableSlotsInTheRow = Math.max(
                Math.max(slotsInPCCase.stream().filter(c -> c.getX() == 0).count(),
                        slotsInPCCase.stream().filter(c -> c.getY() == 0).count()),
                Math.max(slotsInPCCase.stream().filter(c -> c.getX() == -1).count(),
                        slotsInPCCase.stream().filter(c -> c.getY() == -1).count())
        );

        if (slotsToConsume > maxAvailableSlotsInTheRow) {
            throw new ComponentCompatibilityServiceException("Not enough contiguous slots for radiator. Required: " + slotsToConsume + ", available in row: " + maxAvailableSlotsInTheRow);
        }


        var places = new ArrayList<>(pc.getPcCase().getComponentPlacements().stream()
                .filter(component -> component.getComponentType() == ComponentType.CaseCooler)
                .toList());

        int removedElements = 0;
        int i = 0;
        while (removedElements < slotsToConsume && i < places.size()) {
            if (places.get(i).getMaxSize().getHeight() >= cpuCooler.getSize().getHeight()) {
                places.remove(i);
                removedElements++;
            } else {
                i++;
            }
        }


        List<CaseCoolerDTO> coolersLeft = pc.getCoolers() != null ? new ArrayList<>(pc.getCoolers()) : new ArrayList<>();
        for (var place : places) {
            for (int j = 0; j < coolersLeft.size(); j++) {
                if (place.getMaxSize().getHeight() >= coolersLeft.get(j).getSize().getHeight()) {
                    coolersLeft.remove(j);
                    break;
                }
            }
        }

        if (!coolersLeft.isEmpty()) {
            throw new ComponentCompatibilityServiceException(
                    "Not enough fan slots available. After installing the Liquid Cooler, " + coolersLeft.size() + " case fans no longer fit (Overflow)."
            );
        }


        if (pc.getCpu().getPowerDraw() > cpuCooler.getMaxTDP()) {
            throw new ComponentCompatibilityServiceException("CPU cooler TDP (" + cpuCooler.getMaxTDP() + "W) is insufficient for CPU power draw (" + pc.getCpu().getPowerDraw() + "W)");
        }


        if (!pc.getMotherboard().getSocketType().equals(cpuCooler.getCpuSocket())) {
            throw new ComponentCompatibilityServiceException("CPU cooler socket (" + cpuCooler.getCpuSocket() + ") does not match motherboard socket (" + pc.getMotherboard().getSocketType() + ")");
        }

        return true;
    }
    public boolean isPSUCompatible(PCDTO pc, PSUDTO psu) {
        if (psu == null) return true;

        int totalPowerDraw = 0;


        if (pc.getCpu() != null) totalPowerDraw += pc.getCpu().getPowerDraw();
        if (pc.getGpu() != null) totalPowerDraw += pc.getGpu().getPowerDraw();
        if (pc.getMotherboard() != null) totalPowerDraw += pc.getMotherboard().getPowerDraw();
        if (pc.getRamKit() != null) totalPowerDraw += pc.getRamKit().getPowerDraw();
        if (pc.getStorage() != null) totalPowerDraw += pc.getStorage().getPowerDraw() * pc.getStorage().getStorageCount();
        if (pc.getCpuCooler() != null) totalPowerDraw += pc.getCpuCooler().getPowerDraw();


        if (pc.getCoolers() != null) {
            totalPowerDraw += pc.getCoolers().stream()
                    .mapToInt(cooler -> cooler.getPowerDraw() != null ? cooler.getPowerDraw() : 0)
                    .sum();
        }

        boolean isCompatible = psu.getPsuWattage() >= totalPowerDraw * 1.2 + 100;

        if (!isCompatible) {
            throw new ComponentCompatibilityServiceException(
                    "PSU Wattage (" + psu.getPsuWattage() + "W) is insufficient for Total System Power (" + totalPowerDraw + "W)"
            );
        }

        return true;
    }
}
