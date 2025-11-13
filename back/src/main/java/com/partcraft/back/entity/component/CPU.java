package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.CPUDTO;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cpuSocketType;
    private String cpuBrand;
    private String cpuModel;
    private Integer cpuCores;
    private Integer cpuThreads;
    private Double cpuBaseClockGhz;
    private Double cpuBoostClockGhz;
    private String pictureUrl;
    @Embedded
    private Size size;
    private Integer powerDraw;

    public CPU(CPUDTO dto) {
        this.cpuBrand = dto.getCpuBrand();
        this.cpuModel = dto.getCpuModel();
        this.cpuCores = dto.getCpuCores();
        this.cpuSocketType = dto.getSocketType();
        this.cpuThreads = dto.getCpuThreads();
        this.cpuBaseClockGhz = dto.getCpuBaseClockGhz();
        this.cpuBoostClockGhz = dto.getCpuBoostClockGhz();
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
        this.powerDraw = dto.getPowerDraw();
    }

    // Optionally, add a helper method to fetch prices for this CPU using the generic price structure.
}
