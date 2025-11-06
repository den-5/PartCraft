package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.PSUDTO;
import com.partcraft.back.entity.component.PSU;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.PSUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/psu")
public class PSUController extends ComponentController<PSU, PSUDTO, Long> {

    @Autowired
    private PSUService psuService;

    @Override
    protected ComponentService<PSU, PSUDTO, Long> getService() {
        return psuService;
    }
}

