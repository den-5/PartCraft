package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RAMKitDTO {
    private Long id;
    private Integer ramSizeGb;
    private String ramType;
    private Integer ramSpeedMhz;
    private Integer ramSticksCount;
    private String pictureUrl;
}

