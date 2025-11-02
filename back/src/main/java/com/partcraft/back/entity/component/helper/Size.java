package com.partcraft.back.entity.component.helper;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Size {
    private Double width;
    private Double length;
    private Double height;
}

