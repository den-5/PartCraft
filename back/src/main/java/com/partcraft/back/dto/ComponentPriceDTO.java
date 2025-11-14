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
    private Long id;
    private Double value;
    private LocalDate time;
    private Location location;
    private String componentType;
    private Long componentId;
}
