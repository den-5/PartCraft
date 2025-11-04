package com.partcraft.back.entity;

import com.partcraft.back.entity.component.*;
import com.partcraft.back.util.VisibilityState;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "pcs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;
    private String description;
    private String purpose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpu_id")
    private CPU cpu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpu_id")
    private GPU gpu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ramkit_id")
    private RAMKit ramKit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psu_id")
    private PSU psu;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pc_case_coolers",
            joinColumns = @JoinColumn(name = "pc_id"),
            inverseJoinColumns = @JoinColumn(name = "case_cooler_id")
    )
    private List<CaseCooler> coolers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpu_cooler_id")
    private CPUCooler cpuCooler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motherboard_id")
    private MotherBoard motherboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case pcCase;

    private Double benchmarkScore;
    private Double temperatureIdleC;
    private Double temperatureLoadC;
    private Double noiseLevelDb;

    private Double estimatedValueUsd;
    private Double totalPowerDrawW;

    private Instant createdAt;
    private Instant updatedAt;
    private String location;
    @Enumerated(EnumType.STRING)
    private VisibilityState visibility;

    @ElementCollection
    private List<String> tags;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
