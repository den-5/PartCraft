package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPUDTO {
    private Long id;
    private String cpuBrand;
    private String cpuModel;
    private Integer cpuCores;
    private Integer cpuThreads;
    private Double cpuBaseClockGhz;
    private Double cpuBoostClockGhz;
    private String pictureUrl;
}

