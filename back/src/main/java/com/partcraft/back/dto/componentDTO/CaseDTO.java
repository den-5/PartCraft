package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseDTO {
    private Long id;
    private String caseModel;
    private String caseColor;
    private String rgbSetup;
    private String pictureUrl;
    private List<Long> componentPlacementIds;
}

