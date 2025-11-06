package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.CPUCoolerDTO;
import com.partcraft.back.entity.component.helper.Size;
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
    private String coolingType;
    private Integer fanCount;
    private Color coolingColor;
    private String PCCaseType;
    private String pictureUrl;
    @Embedded
    private Size size;
    private Integer powerDraw;

    public CPUCooler(CPUCoolerDTO dto) {
        this.coolingType = dto.getCoolingType();
        this.fanCount = dto.getFanCount();
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
