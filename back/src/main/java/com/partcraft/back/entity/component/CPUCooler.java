package com.partcraft.back.entity.component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
