package com.partcraft.back.entity;

import com.partcraft.back.enums.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "component_price",
        uniqueConstraints = @UniqueConstraint(columnNames = {"component_id", "component_type", "time"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price_value")
    private Double value;

    private LocalDate time;

    @Enumerated(EnumType.STRING)
    private Location location;

    @Column(name = "component_type")
    private String componentType; // e.g., "CPU", "GPU", etc.

    @Column(name = "component_id")
    private Long componentId;     // id of the component
}
