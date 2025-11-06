package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.GPUDTO;
import com.partcraft.back.entity.component.GPU;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.GPUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gpu")
public class GPUController extends ComponentController<GPU, GPUDTO, Long> {

    @Autowired
    private GPUService gpuService;

    @Override
    protected ComponentService<GPU, GPUDTO, Long> getService() {
        return gpuService;
    }
}

