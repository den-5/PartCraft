package com.partcraft.back.dto.PC;

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
    private Long cpuId;
    private Long gpuId;
    private Long ramKitId;
    private Long storageId;
    private Long psuId;
    private List<Long> coolerIds;
    private Long cpuCoolerId;
    private Long motherboardId;
    private Long pcCaseId;
    private Double benchmarkScore;
    private Double temperatureIdleC;
    private Double temperatureLoadC;
    private Double noiseLevelDb;
    private Double estimatedValueUsd;
    private Double totalPowerDrawW;
    private Instant createdAt;
    private Instant updatedAt;
    private String location;
    private String visibility;
    private List<String> tags;
}
