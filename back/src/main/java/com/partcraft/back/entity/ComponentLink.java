package com.partcraft.back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "component_links")
@Getter
@Setter
@NoArgsConstructor
public class ComponentLink {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String componentType; // e.g., "CPU", "GPU"
    private Long componentId;     // id of the component
    private String url;           // link to fetch price

}
