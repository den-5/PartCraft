package com.partcraft.back.service.helper;

import com.partcraft.back.dto.PC.CreateOrUpdatePCDTO;
import com.partcraft.back.entity.PC;
import com.partcraft.back.entity.component.CaseCooler;
import com.partcraft.back.exception.service.PCServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetPCComponentsManager {
    private final ComponentRepositoryManager components;

    public SetPCComponentsManager(ComponentRepositoryManager components) {
        this.components = components;
    }

    public void setAllComponents(PC pc, CreateOrUpdatePCDTO pcDTO) {
        setCpu(pc, pcDTO.getCpuId());
        setGpu(pc, pcDTO.getGpuId());
        setRamKit(pc, pcDTO.getRamKitId());
        setStorage(pc, pcDTO.getStorageId());
        setPsu(pc, pcDTO.getPsuId());
        setCpuCooler(pc, pcDTO.getCpuCoolerId());
        setMotherboard(pc, pcDTO.getMotherboardId());
        setPcCase(pc, pcDTO.getPcCaseId());
        setCoolers(pc, pcDTO.getCoolerIds());
    }

    public void setCpu(PC pc, Long cpuId) {
        if (cpuId != null) {
            var cpu = components.getCpuRepository().findById(cpuId)
                    .orElseThrow(() -> new PCServiceException("CPU with id " + cpuId + " not found"));
            pc.setCpu(cpu);
        }
    }

    public void setGpu(PC pc, Long gpuId) {
        if (gpuId != null) {
            var gpu = components.getGpuRepository().findById(gpuId)
                    .orElseThrow(() -> new PCServiceException("GPU with id " + gpuId + " not found"));
            pc.setGpu(gpu);
        }
    }

    public void setRamKit(PC pc, Long ramKitId) {
        if (ramKitId != null) {
            var ramKit = components.getRamKitRepository().findById(ramKitId)
                    .orElseThrow(() -> new PCServiceException("RAM Kit with id " + ramKitId + " not found"));
            pc.setRamKit(ramKit);
        }
    }

    public void setStorage(PC pc, Long storageId) {
        if (storageId != null) {
            var storage = components.getStorageRepository().findById(storageId)
                    .orElseThrow(() -> new PCServiceException("Storage with id " + storageId + " not found"));
            pc.setStorage(storage);
        }
    }

    public void setPsu(PC pc, Long psuId) {
        if (psuId != null) {
            var psu = components.getPsuRepository().findById(psuId)
                    .orElseThrow(() -> new PCServiceException("PSU with id " + psuId + " not found"));
            pc.setPsu(psu);
        }
    }

    public void setCpuCooler(PC pc, Long cpuCoolerId) {
        if (cpuCoolerId != null) {
            var cpuCooler = components.getCpuCoolerRepository().findById(cpuCoolerId)
                    .orElseThrow(() -> new PCServiceException("CPU Cooler with id " + cpuCoolerId + " not found"));
            pc.setCpuCooler(cpuCooler);
        }
    }

    public void setMotherboard(PC pc, Long motherboardId) {
        if (motherboardId != null) {
            var motherboard = components.getMotherBoardRepository().findById(motherboardId)
                    .orElseThrow(() -> new PCServiceException("Motherboard with id " + motherboardId + " not found"));
            pc.setMotherboard(motherboard);
        }
    }

    public void setPcCase(PC pc, Long pcCaseId) {
        if (pcCaseId != null) {
            var pcCase = components.getCaseRepository().findById(pcCaseId)
                    .orElseThrow(() -> new PCServiceException("Case with id " + pcCaseId + " not found"));
            pc.setPcCase(pcCase);
        }
    }

    public void setCoolers(PC pc, List<Long> coolerIds) {
        if (coolerIds != null && !coolerIds.isEmpty()) {
            List<CaseCooler> caseCoolers = new ArrayList<>();
            for (var coolerId : coolerIds) {
                var caseCooler = components.getCaseCoolerRepository().findById(coolerId)
                        .orElseThrow(() -> new PCServiceException("Cooler with id " + coolerId + " not found"));
                caseCoolers.add(caseCooler);
            }
            pc.setCoolers(caseCoolers);
        }
    }

}
