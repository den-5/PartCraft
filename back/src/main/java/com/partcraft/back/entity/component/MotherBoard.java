package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.MotherBoardDTO;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "motherboard")
public class MotherBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String motherboardBrand;
    private String motherboardModel;
    private String chipset;
    private String socketType;
    private String pictureUrl;

    @Column(name = "memory_type")
    private String memoryType;

    @Embedded
    private Size size;
    private Integer powerDraw;

    public MotherBoard(MotherBoardDTO dto) {
        this.motherboardBrand = dto.getMotherboardBrand();
        this.motherboardModel = dto.getMotherboardModel();
        this.chipset = dto.getChipset();
        this.memoryType = dto.getMemoryType();
        this.socketType = dto.getSocketType();
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
        this.powerDraw = dto.getPowerDraw();
    }

    // Optionally, add a helper method to fetch prices for this MotherBoard using the generic price structure.
}
