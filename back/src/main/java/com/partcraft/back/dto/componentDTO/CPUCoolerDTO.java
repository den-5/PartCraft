package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPUCoolerDTO {
    private Long id;
    private String coolingType;
    private Integer fanCount;
    private String coolingColor;
    private String pcCaseType;
    private String pictureUrl;
}

