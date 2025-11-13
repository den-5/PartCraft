package com.partcraft.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentLinkDTO {
    private Long id;
    private String componentType;
    private Long componentId;
    private String url;
}

