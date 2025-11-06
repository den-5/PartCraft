package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.RAMKitDTO;
import com.partcraft.back.entity.component.RAMKit;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.RAMKitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ram-kit")
public class RAMKitController extends ComponentController<RAMKit, RAMKitDTO, Long> {

    @Autowired
    private RAMKitService ramKitService;

    @Override
    protected ComponentService<RAMKit, RAMKitDTO, Long> getService() {
        return ramKitService;
    }
}

