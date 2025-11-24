package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.CPUDTO;
import com.partcraft.back.entity.component.CPU;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.CPUService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cpu")
@Tag(name = "CPU", description = "Endpoints for managing CPU (Central Processing Unit) components")
@SecurityRequirement(name = "cookieAuth")
public class CPUController extends ComponentController<CPU, CPUDTO, Long> {

    @Autowired
    private CPUService cpuService;

    @Override
    protected ComponentService<CPU, CPUDTO, Long> getService() {
        return cpuService;
    }
}

