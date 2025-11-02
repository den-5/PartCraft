package com.partcraft.back.service.helper;

import com.partcraft.back.repository.component.*;
import org.springframework.stereotype.Component;

@Component
public class ComponentRepositoryManager {
    private final CaseRepository caseRepository;
    private final CaseCoolerRepository caseCoolerRepository;
    private final CPURepository cpuRepository;
    private final CPUCoolerRepository cpuCoolerRepository;
    private final GPURepository gpuRepository;
    private final MotherBoardRepository motherBoardRepository;
    private final PSURepository psuRepository;
    private final RAMKitRepository ramKitRepository;
    private final StorageRepository storageRepository;
    private final ComponentPlacementRepository componentPlacementRepository;

    public ComponentRepositoryManager(CaseRepository caseRepository,
                                      CaseCoolerRepository caseCoolerRepository,
                                      CPURepository cpuRepository,
                                      CPUCoolerRepository cpuCoolerRepository,
                                      GPURepository gpuRepository,
                                      MotherBoardRepository motherBoardRepository,
                                      PSURepository psuRepository,
                                      RAMKitRepository ramKitRepository,
                                      StorageRepository storageRepository,
                                      ComponentPlacementRepository componentPlacementRepository) {
        this.caseRepository = caseRepository;
        this.caseCoolerRepository = caseCoolerRepository;
        this.cpuRepository = cpuRepository;
        this.cpuCoolerRepository = cpuCoolerRepository;
        this.gpuRepository = gpuRepository;
        this.motherBoardRepository = motherBoardRepository;
        this.psuRepository = psuRepository;
        this.ramKitRepository = ramKitRepository;
        this.storageRepository = storageRepository;
        this.componentPlacementRepository = componentPlacementRepository;
    }

    public CaseRepository getCaseRepository() {
        return caseRepository;
    }

    public CaseCoolerRepository getCaseCoolerRepository() {
        return caseCoolerRepository;
    }

    public CPURepository getCpuRepository() {
        return cpuRepository;
    }

    public CPUCoolerRepository getCpuCoolerRepository() {
        return cpuCoolerRepository;
    }

    public GPURepository getGpuRepository() {
        return gpuRepository;
    }

    public MotherBoardRepository getMotherBoardRepository() {
        return motherBoardRepository;
    }

    public PSURepository getPsuRepository() {
        return psuRepository;
    }

    public RAMKitRepository getRamKitRepository() {
        return ramKitRepository;
    }

    public StorageRepository getStorageRepository() {
        return storageRepository;
    }

    public ComponentPlacementRepository getComponentPlacementRepository() {
        return componentPlacementRepository;
    }
}
