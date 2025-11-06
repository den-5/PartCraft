package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.StorageDTO;
import com.partcraft.back.entity.component.Storage;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/storage")
public class StorageController extends ComponentController<Storage, StorageDTO, Long> {

    @Autowired
    private StorageService storageService;

    @Override
    protected ComponentService<Storage, StorageDTO, Long> getService() {
        return storageService;
    }
}

