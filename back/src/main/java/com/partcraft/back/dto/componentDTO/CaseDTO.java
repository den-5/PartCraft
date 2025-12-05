package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.Case;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseDTO {
    private Long id;
    private String caseModel;
    private String caseColor;
    private String rgbSetup;
    private String pictureUrl;
    private Size size;
    private Integer powerDraw;
    private List<ComponentPlacementDTO> componentPlacements;

    public CaseDTO(Case pcCase) {
        this.id = pcCase.getId();
        this.caseModel = pcCase.getCaseModel();
        this.caseColor = pcCase.getCaseColor();
        this.rgbSetup = pcCase.getRgbSetup();
        this.powerDraw = pcCase.getPowerDraw();
        this.pictureUrl = pcCase.getPictureUrl();
        this.size = pcCase.getSize() != null ?
                new Size(pcCase.getSize().getWidth(), pcCase.getSize().getLength(), pcCase.getSize().getHeight()) : null;
        this.componentPlacements = pcCase.getComponentPlacements() != null ? pcCase.getComponentPlacements().stream().map(ComponentPlacementDTO::new).toList() : null;
    }
}
