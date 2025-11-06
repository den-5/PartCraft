package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.GPUDTO;
import com.partcraft.back.entity.component.GPU;
import com.partcraft.back.repository.component.GPURepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class GPUService extends ComponentService<GPU, GPUDTO, Long> {

    @Autowired
    private GPURepository gpuRepository;

    @Override
    protected JpaRepository<GPU, Long> getRepository() {
        return gpuRepository;
    }

    @Override
    protected GPU toEntity(GPUDTO dto) {
        return new GPU(dto);
    }

    @Override
    protected GPUDTO toDTO(GPU entity) {
        return new GPUDTO(entity);
    }
}

