package com.partcraft.back.dto;

import com.partcraft.back.entity.ComponentPrice;
import com.partcraft.back.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ComponentPriceDTO {
    private Double value;
    private LocalDate time;
    private Location location;
    private String componentType;
    private Long componentId;

    public ComponentPriceDTO(ComponentPrice price) {
        this.value = price.getValue();
        this.time = price.getTime();
        this.location = price.getLocation();
        this.componentType = price.getComponentType();
        this.componentId = price.getComponentId();
    }
}
