package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.PSU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PSUDTO {
    private Long id;
    private String psuModel;
    private Integer psuWattage;
    private String pictureUrl;
    private Size size;
    private Integer powerDraw;

    public PSUDTO(PSU psu) {
        this.id = psu.getId();
        this.psuModel = psu.getPsuModel();
        this.psuWattage = psu.getPsuWattage();
        this.pictureUrl = psu.getPictureUrl();
        if (psu.getSize() != null) {
            this.size = new Size(psu.getSize().getWidth(), psu.getSize().getLength(), psu.getSize().getHeight());
        } else {
            this.size = null;
        }
        this.powerDraw = psu.getPowerDraw();
    }
}
