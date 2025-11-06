package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.CaseCoolerDTO;
import com.partcraft.back.entity.component.CaseCooler;
import com.partcraft.back.repository.component.CaseCoolerRepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CaseCoolerService extends ComponentService<CaseCooler, CaseCoolerDTO, Long> {

    @Autowired
    private CaseCoolerRepository caseCoolerRepository;

    @Override
    protected JpaRepository<CaseCooler, Long> getRepository() {
        return caseCoolerRepository;
    }

    @Override
    protected CaseCooler toEntity(CaseCoolerDTO dto) {
        return new CaseCooler(dto);
    }

    @Override
    protected CaseCoolerDTO toDTO(CaseCooler entity) {
        return new CaseCoolerDTO(entity);
    }
}

