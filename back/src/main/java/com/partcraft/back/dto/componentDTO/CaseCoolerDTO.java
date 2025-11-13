package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.CaseCooler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCoolerDTO {
    private Long id;
    private Long pcId;
    private Integer fanSize;
    private String coolingColor;
    private String pictureUrl;
    private Integer powerDraw;
    private Size size;

    public CaseCoolerDTO(CaseCooler cooler) {
        this.id = cooler.getId();
        this.pcId = cooler.getPc() != null ? cooler.getPc().getId() : null;
        this.fanSize = cooler.getFanSize();
        this.coolingColor = cooler.getCoolingColor() != null ? cooler.getCoolingColor().toString() : null;
        this.pictureUrl = cooler.getPictureUrl();
        if (cooler.getSize() != null) {
            this.size = new Size(cooler.getSize().getWidth(), cooler.getSize().getLength(), cooler.getSize().getHeight());
        } else {
            this.size = null;
        }
    }
}
