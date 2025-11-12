package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.CPUCooler;
import com.partcraft.back.enums.CoolingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPUCoolerDTO {
    private Long id;
    private CoolingType coolingType;
    private String cpuSocket;
    private Integer fanCount;
    private String coolingColor;
    private String pcCaseType;
    private Integer caseCoolerSlotsRequired;
    private String pictureUrl;
    private Size size;
    private Integer powerDraw;
    private Long maxTDP;

    public CPUCoolerDTO(CPUCooler cpuCooler) {
        this.id = cpuCooler.getId();
        this.cpuSocket = cpuCooler.getCpuSocket();
        this.coolingType = cpuCooler.getCoolingType();
        this.fanCount = cpuCooler.getFanCount();
        this.caseCoolerSlotsRequired = cpuCooler.getCaseCoolerSlotsRequired();
        this.coolingColor = cpuCooler.getCoolingColor() != null ? cpuCooler.getCoolingColor().toString() : null;
        this.pcCaseType = cpuCooler.getPCCaseType();
        this.pictureUrl = cpuCooler.getPictureUrl();
        this.maxTDP = cpuCooler.getMaxTDP();
        if (cpuCooler.getSize() != null) {
            this.size = new Size(cpuCooler.getSize().getWidth(), cpuCooler.getSize().getLength(), cpuCooler.getSize().getHeight());
        } else {
            this.size = null;
        }
        this.powerDraw = cpuCooler.getPowerDraw();
    }
}
