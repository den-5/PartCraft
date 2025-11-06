package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.CPUDTO;
import com.partcraft.back.entity.component.CPU;
import com.partcraft.back.repository.component.CPURepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CPUService extends ComponentService<CPU, CPUDTO, Long> {

    @Autowired
    private CPURepository cpuRepository;

    @Override
    protected JpaRepository<CPU, Long> getRepository() {
        return cpuRepository;
    }

    @Override
    protected CPU toEntity(CPUDTO dto) {
        return new CPU(dto);
    }

    @Override
    protected CPUDTO toDTO(CPU entity) {
        return new CPUDTO(entity);
    }
}

