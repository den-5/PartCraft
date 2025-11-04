package com.partcraft.back.dto.PC;

import com.partcraft.back.dto.componentDTO.*;
import com.partcraft.back.util.VisibilityState;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PCDTO {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String purpose;
    private CPUDTO cpu;
    private GPUDTO gpu;
    private RAMKitDTO ramKit;
    private StorageDTO storage;
    private PSUDTO psu;
    private List<CaseCoolerDTO> coolers;
    private CPUCoolerDTO cpuCooler;
    private MotherBoardDTO motherboard;
    private CaseDTO pcCase;
    private Double benchmarkScore;
    private Double temperatureIdleC;
    private Double temperatureLoadC;
    private Double noiseLevelDb;
    private Double estimatedValueUsd;
    private Double totalPowerDrawW;
    private Instant createdAt;
    private Instant updatedAt;
    private String location;
    private VisibilityState visibility;
    private List<String> tags;
}
