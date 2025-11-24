package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.GPUDTO;
import com.partcraft.back.entity.component.GPU;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.GPUService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gpu")
@Tag(name = "GPU", description = "Endpoints for managing GPU (Graphics Processing Unit) components")
@SecurityRequirement(name = "cookieAuth")
public class GPUController extends ComponentController<GPU, GPUDTO, Long> {

    @Autowired
    private GPUService gpuService;

    @Override
    protected ComponentService<GPU, GPUDTO, Long> getService() {
        return gpuService;
    }
}

