package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.MotherBoardDTO;
import com.partcraft.back.entity.component.MotherBoard;
import com.partcraft.back.repository.component.MotherBoardRepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class MotherBoardService extends ComponentService<MotherBoard, MotherBoardDTO, Long> {

    @Autowired
    private MotherBoardRepository motherBoardRepository;

    @Override
    protected JpaRepository<MotherBoard, Long> getRepository() {
        return motherBoardRepository;
    }

    @Override
    protected MotherBoard toEntity(MotherBoardDTO dto) {
        return new MotherBoard(dto);
    }

    @Override
    protected MotherBoardDTO toDTO(MotherBoard entity) {
        return new MotherBoardDTO(entity);
    }
}

