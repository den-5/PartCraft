package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.CPU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPUDTO {
    private Long id;
    private String socketType;
    private String cpuBrand;
    private String cpuModel;
    private Integer cpuCores;
    private Integer cpuThreads;
    private Double cpuBaseClockGhz;
    private Double cpuBoostClockGhz;
    private String pictureUrl;
    private Size size;
    private Integer powerDraw;

    public CPUDTO(CPU cpu) {
        this.id = cpu.getId();
        this.socketType = cpu.getCpuSocketType();
        this.cpuBrand = cpu.getCpuBrand();
        this.cpuModel = cpu.getCpuModel();
        this.cpuCores = cpu.getCpuCores();
        this.cpuThreads = cpu.getCpuThreads();
        this.cpuBaseClockGhz = cpu.getCpuBaseClockGhz();
        this.cpuBoostClockGhz = cpu.getCpuBoostClockGhz();
        this.pictureUrl = cpu.getPictureUrl();
        if (cpu.getSize() != null) {
            this.size = new Size(cpu.getSize().getWidth(), cpu.getSize().getLength(), cpu.getSize().getHeight());
        } else {
            this.size = null;
        }
        this.powerDraw = cpu.getPowerDraw();
    }
}
