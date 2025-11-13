package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.PSUDTO;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PSU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String psuModel;
    private Integer psuWattage;
    private String pictureUrl;
    @Embedded
    private Size size;
    private Integer powerDraw;

    public PSU(PSUDTO dto) {
        this.psuModel = dto.getPsuModel();
        this.psuWattage = dto.getPsuWattage();
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
        this.powerDraw = dto.getPowerDraw();
    }

    // Optionally, add a helper method to fetch prices for this PSU using the generic price structure.
}
