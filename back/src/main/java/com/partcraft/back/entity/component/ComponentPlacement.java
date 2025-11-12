package com.partcraft.back.entity.component;

import com.partcraft.back.dto.componentDTO.helper.Size;
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

    @Column(nullable = false)
    private String componentType; // e.g., "CPU", "GPU", etc.
    @Embedded
    private Size maxSize;

    private Double x;
    private Double y;
    private Double z;
    private Double rotation;
    // Add more fields as needed for rendering
}
