package com.partcraft.back.dto.componentDTO;

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
}

