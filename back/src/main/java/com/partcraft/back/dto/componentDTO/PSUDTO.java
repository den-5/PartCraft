package com.partcraft.back.dto.componentDTO;

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
}

