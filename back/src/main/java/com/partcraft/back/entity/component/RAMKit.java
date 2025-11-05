package com.partcraft.back.entity.component;

import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RAMKit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ramSizeGb;
    private String ramType;
    private Integer ramSpeedMhz;
    private Integer ramSticksCount;
    private String pictureUrl;
    private Integer powerDraw;
    @Embedded
    private Size size;
}
