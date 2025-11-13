package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.RAMKitDTO;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RAMKit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer ramSizeGb;
    private String ramType;
    private Integer ramSpeedMhz;
    private Integer ramSticksCount;
    private String pictureUrl;
    @Embedded
    private Size size;
    private Integer powerDraw;

    public RAMKit(RAMKitDTO dto) {
        this.ramSizeGb = dto.getRamSizeGb();
        this.ramType = dto.getRamType();
        this.ramSpeedMhz = dto.getRamSpeedMhz();
        this.ramSticksCount = dto.getRamSticksCount();
        this.pictureUrl = dto.getPictureUrl();
        this.powerDraw = dto.getPowerDraw();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
    }

    // Optionally, add a helper method to fetch prices for this RAMKit using the generic price structure.
}
