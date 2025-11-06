package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.CPUCoolerDTO;
import com.partcraft.back.entity.component.CPUCooler;
import com.partcraft.back.repository.component.CPUCoolerRepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CPUCoolerService extends ComponentService<CPUCooler, CPUCoolerDTO, Long> {

    @Autowired
    private CPUCoolerRepository cpuCoolerRepository;

    @Override
    protected JpaRepository<CPUCooler, Long> getRepository() {
        return cpuCoolerRepository;
    }

    @Override
    protected CPUCooler toEntity(CPUCoolerDTO dto) {
        return new CPUCooler(dto);
    }

    @Override
    protected CPUCoolerDTO toDTO(CPUCooler entity) {
        return new CPUCoolerDTO(entity);
    }
}

