package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.Case;
import com.partcraft.back.entity.component.ComponentPlacement;
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
    private List<Long> componentPlacementIds;

    public CaseDTO(Case pcCase) {
        this.id = pcCase.getId();
        this.caseModel = pcCase.getCaseModel();
        this.caseColor = pcCase.getCaseColor();
        this.rgbSetup = pcCase.getRgbSetup();
        this.pictureUrl = pcCase.getPictureUrl();
        if (pcCase.getSize() != null) {
            this.size = new Size(pcCase.getSize().getWidth(), pcCase.getSize().getLength(), pcCase.getSize().getHeight());
        } else {
            this.size = null;
        }
        this.componentPlacementIds = pcCase.getComponentPlacements() != null ? pcCase.getComponentPlacements().stream().map(ComponentPlacement::getId).toList() : null;
    }
}
