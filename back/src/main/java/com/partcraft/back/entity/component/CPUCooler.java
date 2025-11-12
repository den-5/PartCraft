package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.CPUCoolerDTO;
import com.partcraft.back.entity.component.helper.Size;
import com.partcraft.back.enums.CoolingType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CPUCooler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private CoolingType coolingType;
    private String cpuSocket;
    private Integer fanCount;
    private Color coolingColor;
    private Integer caseCoolerSlotsRequired;
    private String PCCaseType;
    private String pictureUrl;
    private Long maxTDP;
    @Embedded
    private Size size;
    private Integer powerDraw;

    public CPUCooler(CPUCoolerDTO dto) {
        this.cpuSocket = dto.getCpuSocket();
        this.coolingType = dto.getCoolingType();
        this.fanCount = dto.getFanCount();
        this.caseCoolerSlotsRequired = dto.getCaseCoolerSlotsRequired();
        if (dto.getCoolingColor() != null) {
            this.coolingColor = Color.decode(dto.getCoolingColor());
        }
        this.PCCaseType = dto.getPcCaseType();
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
        this.powerDraw = dto.getPowerDraw();
    }
}
