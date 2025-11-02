package com.partcraft.back.dto.componentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MotherBoardDTO {
    private Long id;
    private String motherboardBrand;
    private String motherboardModel;
    private String chipset;
    private String socketType;
    private String pictureUrl;
}

