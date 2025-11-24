package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.PSUDTO;
import com.partcraft.back.entity.component.PSU;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.PSUService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/psu")
@Tag(name = "PSU", description = "Endpoints for managing PSU (Power Supply Unit) components")
@SecurityRequirement(name = "cookieAuth")
public class PSUController extends ComponentController<PSU, PSUDTO, Long> {

    @Autowired
    private PSUService psuService;

    @Override
    protected ComponentService<PSU, PSUDTO, Long> getService() {
        return psuService;
    }
}

