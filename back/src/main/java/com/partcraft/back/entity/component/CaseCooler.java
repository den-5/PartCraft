package com.partcraft.back.entity.component;

import com.partcraft.back.entity.PC;
import jakarta.persistence.*;

import java.awt.*;

@Entity
public class CaseCooler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pc_id")
    private PC pc;

    private String coolingType;
    private Integer fanCount;
    private Color coolingColor;
}
