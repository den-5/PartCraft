package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.CaseCoolerDTO;
import com.partcraft.back.entity.PC;
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
public class CaseCooler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pc_id")
    private PC pc;

    private String coolingType;
    private Integer fanSize;
    private Color coolingColor;
    private String pictureUrl;
    @Embedded
    private Size size;

    public CaseCooler(CaseCoolerDTO dto) {
        this.coolingType = dto.getCoolingType();
        this.fanSize = dto.getFanSize();
        if (dto.getCoolingColor() != null) {
            this.coolingColor = Color.decode(dto.getCoolingColor());
        }
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
    }
}
