package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.CaseCoolerDTO;
import com.partcraft.back.entity.component.CaseCooler;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.CaseCoolerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/case-cooler")
@Tag(name = "Case Cooler", description = "Endpoints for managing case fan/cooler components")
@SecurityRequirement(name = "cookieAuth")
public class CaseCoolerController extends ComponentController<CaseCooler, CaseCoolerDTO, Long> {

    @Autowired
    private CaseCoolerService caseCoolerService;

    @Override
    protected ComponentService<CaseCooler, CaseCoolerDTO, Long> getService() {
        return caseCoolerService;
    }
}

