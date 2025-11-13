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
        uniqueConstraints = @UniqueConstraint(columnNames = {"componentId", "componentType", "time"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double value;
    private LocalDate time;
    private Location location;
    private String componentType; // e.g., "CPU", "GPU", etc.
    private Long componentId;     // id of the component
}
