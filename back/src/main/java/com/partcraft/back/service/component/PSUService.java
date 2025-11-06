package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.PSUDTO;
import com.partcraft.back.entity.component.PSU;
import com.partcraft.back.repository.component.PSURepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PSUService extends ComponentService<PSU, PSUDTO, Long> {

    @Autowired
    private PSURepository psuRepository;

    @Override
    protected JpaRepository<PSU, Long> getRepository() {
        return psuRepository;
    }

    @Override
    protected PSU toEntity(PSUDTO dto) {
        return new PSU(dto);
    }

    @Override
    protected PSUDTO toDTO(PSU entity) {
        return new PSUDTO(entity);
    }
}

