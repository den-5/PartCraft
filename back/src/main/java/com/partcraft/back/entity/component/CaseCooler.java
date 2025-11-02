package com.partcraft.back.entity.component;

import com.partcraft.back.entity.PC;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Entity
@Getter
@Setter
public class CaseCooler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pc_id")
    private PC pc;

    private String coolingType;
    private Integer fanSize;
    private Color coolingColor;
    private String pictureUrl;
}
