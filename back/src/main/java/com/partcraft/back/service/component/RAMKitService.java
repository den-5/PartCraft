package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.RAMKitDTO;
import com.partcraft.back.entity.component.RAMKit;
import com.partcraft.back.repository.component.RAMKitRepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class RAMKitService extends ComponentService<RAMKit, RAMKitDTO, Long> {

    @Autowired
    private RAMKitRepository ramKitRepository;

    @Override
    protected JpaRepository<RAMKit, Long> getRepository() {
        return ramKitRepository;
    }

    @Override
    protected RAMKit toEntity(RAMKitDTO dto) {
        return new RAMKit(dto);
    }

    @Override
    protected RAMKitDTO toDTO(RAMKit entity) {
        return new RAMKitDTO(entity);
    }
}

