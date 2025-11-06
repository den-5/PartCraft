package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.GPUDTO;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gpuBrand;
    private String gpuModel;
    private Integer gpuMemoryGb;
    private String pictureUrl;
    @Embedded
    private Size size;
    private Integer powerDraw;

    public GPU(GPUDTO dto) {
        this.gpuBrand = dto.getGpuBrand();
        this.gpuModel = dto.getGpuModel();
        this.gpuMemoryGb = dto.getGpuMemoryGb();
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
        this.powerDraw = dto.getPowerDraw();
    }
}
