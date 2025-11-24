package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.MotherBoardDTO;
import com.partcraft.back.entity.component.MotherBoard;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.MotherBoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/motherboard")
@Tag(name = "Motherboard", description = "Endpoints for managing motherboard components")
@SecurityRequirement(name = "cookieAuth")
public class MotherBoardController extends ComponentController<MotherBoard, MotherBoardDTO, Long> {

    @Autowired
    private MotherBoardService motherBoardService;

    @Override
    protected ComponentService<MotherBoard, MotherBoardDTO, Long> getService() {
        return motherBoardService;
    }
}

