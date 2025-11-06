package com.partcraft.back.controller.component;

import com.partcraft.back.controller.ComponentController;
import com.partcraft.back.dto.componentDTO.CaseDTO;
import com.partcraft.back.entity.component.Case;
import com.partcraft.back.service.ComponentService;
import com.partcraft.back.service.component.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/case")
public class CaseController extends ComponentController<Case, CaseDTO, Long> {

    @Autowired
    private CaseService caseService;

    @Override
    protected ComponentService<Case, CaseDTO, Long> getService() {
        return caseService;
    }
}

