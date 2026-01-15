export interface UserDTO {
    id: number;
    email: string;
    username: string;
}

export interface UpdateUserDTO {
    email: string;
    username: string;
    password: string;
}

export enum VisibilityState {
    PUBLIC = 'PUBLIC',
    PRIVATE = 'PRIVATE',
    UNLISTED = 'UNLISTED',
}

export interface Size {
    width: number;
    length: number;
    height: number;
}

export enum CoolingType {
    AIR = 'AIR',
    LIQUID = 'LIQUID',
}

export interface BaseComponentDTO {
    id: number;
    pictureUrl: string;
    size: Size | null;
    powerDraw: number;
}

export interface CPUDTO extends BaseComponentDTO {
    socketType: string;
    cpuBrand: string;
    cpuModel: string;
    cpuCores: number;
    cpuThreads: number;
    cpuBaseClockGhz: number;
    cpuBoostClockGhz: number;
    powerDraw: number;
}

export interface GPUDTO extends BaseComponentDTO {
    gpuBrand: string;
    gpuModel: string;
    gpuMemoryGb: number;
    powerDraw: number;
}

export interface RAMKitDTO extends BaseComponentDTO {
    ramSizeGb: number;
    ramType: string;
    ramSpeedMhz: number;
    ramSticksCount: number;
}

export interface StorageDTO extends BaseComponentDTO {
    storageTotalGb: number;
    storageType: string;
    storageCount: number;
}

export interface PSUDTO extends BaseComponentDTO {
    psuModel: string;
    psuWattage: number;
}

export interface CaseCoolerDTO extends BaseComponentDTO {
    fanSize: number;
    coolingColor: string;
}

export interface CPUCoolerDTO extends BaseComponentDTO {
    coolingType: CoolingType;
    cpuSocket: string;
    fanCount: number;
    coolingColor: string;
    pcCaseType: string;
    caseCoolerSlotsRequired: number;
    maxTDP: number;
}

export interface MotherBoardDTO extends BaseComponentDTO {
    motherboardBrand: string;
    motherboardModel: string;
    chipset: string;
    memoryType: string;
    socketType: string;
}

export interface CaseDTO extends BaseComponentDTO {
    caseModel: string;
    caseColor: string;
    rgbSetup: string;
    componentPlacementIds: number[] | null;
}

export interface PCDTO {
    id: number;
    ownerId: number;
    name: string;
    description: string;
    purpose: string;
    cpu: CPUDTO;
    gpu: GPUDTO;
    ramKit: RAMKitDTO;
    storage: StorageDTO;
    psu: PSUDTO;
    coolers: CaseCoolerDTO[];
    cpuCooler: CPUCoolerDTO;
    motherboard: MotherBoardDTO;
    pcCase: CaseDTO;
    benchmarkScore: number;
    temperatureIdleC: number;
    temperatureLoadC: number;
    noiseLevelDb: number;
    estimatedValueUsd: number;
    totalPowerDrawW: number;
    createdAt: string; // ISO date string
    updatedAt: string; // ISO date string
    location: string;
    visibility: VisibilityState;
    tags: string[];
}

export interface CreatePCDTO {
    name: string;
    description: string;
    purpose: string;
    cpuId: number;
    gpuId: number;
    storageId: number;
    ramKitId: number;
    psuId: number;
    coolerIds: number[];
    cpuCoolerId: number;
    motherboardId: number;
    pcCaseId: number;
    location: string;
    visibility: VisibilityState;
}

export interface UpdatePCDTO {
    name: string;
    description: string;
    purpose: string;
    cpuId: number;
    gpuId: number;
    storageId: number;
    ramKitId: number;
    psuId: number;
    coolerIds: number[];
    cpuCoolerId: number;
    motherboardId: number;
    pcCaseId: number;
    visibility: VisibilityState;
    tags: string[];
}
