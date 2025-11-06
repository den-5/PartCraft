package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.CPUDTO;
import com.partcraft.back.entity.component.CPU;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.CPUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cpu")
public class CPUController extends ComponentController<CPU, CPUDTO, Long> {

    @Autowired
    private CPUService cpuService;

    @Override
    protected ComponentService<CPU, CPUDTO, Long> getService() {
        return cpuService;
    }
}

