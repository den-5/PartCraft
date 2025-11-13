package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.StorageDTO;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer storageTotalGb;
    private String storageType;
    private Integer storageCount;
    private String pictureUrl;
    @Embedded
    private Size size;
    private Integer powerDraw;

    public Storage(StorageDTO dto) {
        this.storageTotalGb = dto.getStorageTotalGb();
        this.storageType = dto.getStorageType();
        this.storageCount = dto.getStorageCount();
        this.pictureUrl = dto.getPictureUrl();
        this.powerDraw = dto.getPowerDraw();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
    }

    // Optionally, add a helper method to fetch prices for this Storage using the generic price structure.
}
