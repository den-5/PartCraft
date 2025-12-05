package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.CaseDTO;
import com.partcraft.back.entity.ComponentPlacement;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`case`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String caseModel;
    private String caseColor;
    private String rgbSetup;
    private String pictureUrl;
    @Embedded
    private Size size;
    private Integer powerDraw;
    @OneToMany(mappedBy = "pcCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComponentPlacement> componentPlacements = new ArrayList<>();

    public Case(CaseDTO dto) {
        this.id = dto.getId();
        this.caseModel = dto.getCaseModel();
        this.caseColor = dto.getCaseColor();
        this.rgbSetup = dto.getRgbSetup();
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
        this.componentPlacements = new ArrayList<>();
        if (dto.getComponentPlacements() != null) {
            for (var placementDTO : dto.getComponentPlacements()) {
                ComponentPlacement placement = new ComponentPlacement();
                placement.setId(placementDTO.getComponentId());
                placement.setComponentType(placementDTO.getComponentType());
                if (placementDTO.getMaxSize() != null) {
                    placement.setMaxSize(new Size(
                            placementDTO.getMaxSize().getWidth(),
                            placementDTO.getMaxSize().getLength(),
                            placementDTO.getMaxSize().getHeight()
                    ));
                }
                placement.setX(placementDTO.getX());
                placement.setY(placementDTO.getY());
                placement.setZ(placementDTO.getZ());
                placement.setRotation(placementDTO.getRotation());
                placement.setPcCase(this);
                this.componentPlacements.add(placement);
            }
        }
        this.powerDraw = dto.getPowerDraw();
    }
}
