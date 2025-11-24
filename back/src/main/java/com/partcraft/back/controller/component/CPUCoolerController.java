package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.CPUCoolerDTO;
import com.partcraft.back.entity.component.CPUCooler;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.CPUCoolerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cpu-cooler")
@Tag(name = "CPU Cooler", description = "Endpoints for managing CPU cooler components (air and liquid cooling)")
@SecurityRequirement(name = "cookieAuth")
public class CPUCoolerController extends ComponentController<CPUCooler, CPUCoolerDTO, Long> {

    @Autowired
    private CPUCoolerService cpuCoolerService;

    @Override
    protected ComponentService<CPUCooler, CPUCoolerDTO, Long> getService() {
        return cpuCoolerService;
    }
}

