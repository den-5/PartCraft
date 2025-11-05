package com.partcraft.back.entity.component;

import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer storageTotalGb;
    private String storageType;
    private Integer storageCount;
    private String pictureUrl;
    private Integer powerDraw;
    @Embedded
    private Size size;
}
