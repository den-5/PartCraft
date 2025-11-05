package com.partcraft.back.entity.component;

import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
}
