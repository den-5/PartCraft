package com.partcraft.back.dto.PC;

import lombok.Getter;

import java.util.List;

public interface CreateOrUpdatePCDTO {
    Long getCpuId();

    Long getGpuId();

    Long getRamKitId();

    Long getStorageId();

    Long getPsuId();

    Long getCpuCoolerId();

    Long getMotherboardId();

    Long getPcCaseId();

    List<Long> getCoolerIds();
}

