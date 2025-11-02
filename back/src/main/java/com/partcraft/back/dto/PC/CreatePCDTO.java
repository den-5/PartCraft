package com.partcraft.back.dto.PC;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePCDTO {
    private String name;
    private String description;
    private String purpose;
    private Long cpuId;
    private Long gpuId;
    private Long storageId;
    private Long ramKitId;
    private Long psuId;
    private List<Long> coolerIds;
    private Long cpuCoolerId;
    private Long motherboardId;
    private Long pcCaseId;
    private String location;
    private String visibility;
}
