package com.partcraft.back.entity.component;

import com.partcraft.back.entity.component.helper.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Entity
@Getter
@Setter
public class CPUCooler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String coolingType;
    private Integer fanCount;
    private Color coolingColor;
    private String PCCaseType;
    private String pictureUrl;
    @Embedded
    private Size size;
}
