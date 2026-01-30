package com.partcraft.back.entity;

import com.partcraft.back.entity.component.helper.Size;
import com.partcraft.back.entity.component.Case;
import com.partcraft.back.enums.ComponentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "component_placement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentPlacement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case pcCase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentType componentType;
    @Embedded
    private Size maxSize;

    private Double x;
    private Double y;
    private Double z;
    private Double rotation;
    // Add more fields as needed for rendering
}
