package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.CaseDTO;
import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(mappedBy = "pcCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ComponentPlacement> componentPlacements = new java.util.ArrayList<>();

    public Case(CaseDTO dto) {
        this.caseModel = dto.getCaseModel();
        this.caseColor = dto.getCaseColor();
        this.rgbSetup = dto.getRgbSetup();
        this.pictureUrl = dto.getPictureUrl();
        if (dto.getSize() != null) {
            this.size = new Size(dto.getSize().getWidth(), dto.getSize().getLength(), dto.getSize().getHeight());
        }
        this.componentPlacements = new java.util.ArrayList<>();
    }
}
