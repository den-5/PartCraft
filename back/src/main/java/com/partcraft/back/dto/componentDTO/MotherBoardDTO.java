package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.MotherBoard;
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
    private Size size;

    public MotherBoardDTO(MotherBoard mb) {
        this.id = mb.getId();
        this.motherboardBrand = mb.getMotherboardBrand();
        this.motherboardModel = mb.getMotherboardModel();
        this.chipset = mb.getChipset();
        this.socketType = mb.getSocketType();
        this.pictureUrl = mb.getPictureUrl();
        if (mb.getSize() != null) {
            this.size = new Size(mb.getSize().getWidth(), mb.getSize().getLength(), mb.getSize().getHeight());
        } else {
            this.size = null;
        }
    }
}
