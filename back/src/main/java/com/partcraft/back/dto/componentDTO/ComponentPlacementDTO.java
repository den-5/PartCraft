package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.ComponentPlacement;
import com.partcraft.back.enums.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComponentPlacementDTO {
    private Long componentId;
    private ComponentType componentType;
    private Size maxSize;
    private Double x;
    private Double y;
    private Double z;
    private Double rotation;

    public ComponentPlacementDTO(ComponentPlacement componentPlacement) {
        this.componentId = componentPlacement.getId();
        this.componentType = componentPlacement.getComponentType();
        this.maxSize = componentPlacement.getMaxSize() != null ?
                new Size(componentPlacement.getMaxSize().getWidth(),
                         componentPlacement.getMaxSize().getLength(),
                         componentPlacement.getMaxSize().getHeight()) : null;
        this.x = componentPlacement.getX();
        this.y = componentPlacement.getY();
        this.z = componentPlacement.getZ();
        this.rotation = componentPlacement.getRotation();
    }
}
