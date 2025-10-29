package com.partcraft.back.entity.component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cpuBrand;
    private String cpuModel;
    private Integer cpuCores;
    private Integer cpuThreads;
    private Double cpuBaseClockGhz;
    private Double cpuBoostClockGhz;
}
