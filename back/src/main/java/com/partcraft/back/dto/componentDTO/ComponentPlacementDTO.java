package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentPlacementDTO {
    private Long id;
    private Long caseId;
    private String componentType;
    private Long componentId;
    private Double x;
    private Double y;
    private Double z;
    private Double rotation;
}

