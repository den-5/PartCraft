package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.CPUCooler;
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
    private Size size;
    private Integer powerDraw;

    public CPUCoolerDTO(CPUCooler cpuCooler) {
        this.id = cpuCooler.getId();
        this.coolingType = cpuCooler.getCoolingType();
        this.fanCount = cpuCooler.getFanCount();
        this.coolingColor = cpuCooler.getCoolingColor() != null ? cpuCooler.getCoolingColor().toString() : null;
        this.pcCaseType = cpuCooler.getPCCaseType();
        this.pictureUrl = cpuCooler.getPictureUrl();
        if (cpuCooler.getSize() != null) {
            this.size = new Size(cpuCooler.getSize().getWidth(), cpuCooler.getSize().getLength(), cpuCooler.getSize().getHeight());
        } else {
            this.size = null;
        }
        this.powerDraw = cpuCooler.getPowerDraw();
    }
}
