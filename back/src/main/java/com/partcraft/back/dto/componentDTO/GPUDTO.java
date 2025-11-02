package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPUDTO {
    private Long id;
    private String gpuBrand;
    private String gpuModel;
    private Integer gpuMemoryGb;
    private String pictureUrl;
}

