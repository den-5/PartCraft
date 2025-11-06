package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.CaseDTO;
import com.partcraft.back.entity.component.Case;
import com.partcraft.back.repository.component.CaseRepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CaseService extends ComponentService<Case, CaseDTO, Long> {

    @Autowired
    private CaseRepository caseRepository;

    @Override
    protected JpaRepository<Case, Long> getRepository() {
        return caseRepository;
    }

    @Override
    protected Case toEntity(CaseDTO dto) {
        return new Case(dto);
    }

    @Override
    protected CaseDTO toDTO(Case entity) {
        return new CaseDTO(entity);
    }
}

