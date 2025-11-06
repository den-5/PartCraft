package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.Storage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageDTO {
    private Long id;
    private Integer storageTotalGb;
    private String storageType;
    private Integer storageCount;
    private String pictureUrl;
    private Size size;
    private Integer powerDraw;

    public StorageDTO(Storage storage) {
        this.id = storage.getId();
        this.storageTotalGb = storage.getStorageTotalGb();
        this.storageType = storage.getStorageType();
        this.storageCount = storage.getStorageCount();
        this.pictureUrl = storage.getPictureUrl();
        if (storage.getSize() != null) {
            this.size = new Size(storage.getSize().getWidth(), storage.getSize().getLength(), storage.getSize().getHeight());
        } else {
            this.size = null;
        }
        this.powerDraw = storage.getPowerDraw();
    }
}
