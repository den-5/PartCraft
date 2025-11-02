package com.partcraft.back.entity.component;

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

    @OneToMany(mappedBy = "pcCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ComponentPlacement> componentPlacements = new java.util.ArrayList<>();
}
