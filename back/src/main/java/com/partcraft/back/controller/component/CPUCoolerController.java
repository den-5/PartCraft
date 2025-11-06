package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.CPUCoolerDTO;
import com.partcraft.back.entity.component.CPUCooler;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.CPUCoolerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cpu-cooler")
public class CPUCoolerController extends ComponentController<CPUCooler, CPUCoolerDTO, Long> {

    @Autowired
    private CPUCoolerService cpuCoolerService;

    @Override
    protected ComponentService<CPUCooler, CPUCoolerDTO, Long> getService() {
        return cpuCoolerService;
    }
}

