package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCoolerDTO {
    private Long id;
    private Long pcId;
    private String coolingType;
    private Integer fanSize;
    private String coolingColor;
    private String pictureUrl;
}

