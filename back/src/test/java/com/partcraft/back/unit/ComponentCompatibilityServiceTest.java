package com.partcraft.back.unit;

import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.dto.componentDTO.*;
import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.enums.ComponentType;
import com.partcraft.back.exception.service.ComponentCompatibilityServiceException;
import com.partcraft.back.service.ComponentCompatibilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

public class ComponentCompatibilityServiceTest {
    private ComponentCompatibilityService service;

    @BeforeEach
    void setUp() {
        service = new ComponentCompatibilityService();
    }

    @Test
    void isCpuAndMotherboardCompatible_shouldReturnTrue_whenSocketsMatch() {
        CPUDTO cpu = new CPUDTO();
        cpu.setSocketType("AM4");
        MotherBoardDTO mb = new MotherBoardDTO();
        mb.setSocketType("AM4");
        assertTrue(service.isCpuAndMotherboardCompatible(cpu, mb));
    }

    @Test
    void isCpuAndMotherboardCompatible_shouldThrowException_whenSocketsDoNotMatch() {
        CPUDTO cpu = new CPUDTO();
        cpu.setSocketType("LGA1151");
        MotherBoardDTO mb = new MotherBoardDTO();
        mb.setSocketType("AM4");
        assertThatThrownBy(() -> service.isCpuAndMotherboardCompatible(cpu, mb))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("sockets do not match");
    }

    @Test
    void isCpuAndMotherboardCompatible_shouldThrowException_whenSocketIsNull() {
        CPUDTO cpu = new CPUDTO();
        cpu.setSocketType(null);
        MotherBoardDTO mb = new MotherBoardDTO();
        mb.setSocketType("AM4");
        assertThatThrownBy(() -> service.isCpuAndMotherboardCompatible(cpu, mb))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("socket type is null");
    }

    @Test
    void isMotherboardAndRAMCompatible_shouldReturnTrue_whenMemoryTypesMatch() {
        MotherBoardDTO mb = new MotherBoardDTO();
        mb.setMemoryType("DDR4");
        RAMKitDTO ram = new RAMKitDTO();
        ram.setRamType("DDR4");
        assertTrue(service.isMotherboardAndRAMCompatible(mb, ram));
    }

    @Test
    void isMotherboardAndRAMCompatible_shouldThrowException_whenMemoryTypesDoNotMatch() {
        MotherBoardDTO mb = new MotherBoardDTO();
        mb.setMemoryType("DDR4");
        RAMKitDTO ram = new RAMKitDTO();
        ram.setRamType("DDR3");
        assertThatThrownBy(() -> service.isMotherboardAndRAMCompatible(mb, ram))
                .isInstanceOf(ComponentCompatibilityServiceException.class);
    }

    @Test
    void isMotherboardAndRAMCompatible_shouldThrowException_whenMemoryTypeIsNull() {
        MotherBoardDTO mb = new MotherBoardDTO();
        mb.setMemoryType(null);
        RAMKitDTO ram = new RAMKitDTO();
        ram.setRamType("DDR4");
        assertThatThrownBy(() -> service.isMotherboardAndRAMCompatible(mb, ram))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("memory type is null");
    }

    @Test
    void isGPUAndCaseCompatible_shouldReturnTrue_whenGpuFits() {
        Size gpuSize = new Size(100.0, 200.0, 40.0);
        Size maxSize = new Size(120.0, 250.0, 50.0);
        ComponentPlacementDTO placement = new ComponentPlacementDTO();
        placement.setComponentType(ComponentType.GPU);
        placement.setMaxSize(maxSize);
        GPUDTO gpu = mock(GPUDTO.class);
        when(gpu.getSize()).thenReturn(gpuSize);
        CaseDTO pcCase = mock(CaseDTO.class);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(placement));
        assertTrue(service.isGPUAndCaseCompatible(gpu, pcCase));
    }

    @Test
    void isGPUAndCaseCompatible_shouldReturnFalse_whenGpuDoesNotFit() {
        Size gpuSize = new Size(130.0, 260.0, 60.0);
        Size maxSize = new Size(120.0, 250.0, 50.0);
        ComponentPlacementDTO placement = new ComponentPlacementDTO();
        placement.setComponentType(ComponentType.GPU);
        placement.setMaxSize(maxSize);
        GPUDTO gpu = mock(GPUDTO.class);
        when(gpu.getSize()).thenReturn(gpuSize);
        CaseDTO pcCase = mock(CaseDTO.class);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(placement));
        assertFalse(service.isGPUAndCaseCompatible(gpu, pcCase));
    }

    @Test
    void isGPUAndCaseCompatible_shouldThrowException_whenNoGpuSlot() {
        GPUDTO gpu = mock(GPUDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        when(pcCase.getComponentPlacements()).thenReturn(List.of());
        assertThatThrownBy(() -> service.isGPUAndCaseCompatible(gpu, pcCase))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("GPU component placement not found");
    }

    @Test
    void checkCaseCoolersAndCaseCompatibility_shouldReturnEmpty_whenAllFit() {
        Size size1 = new Size(40.0, 40.0, 40.0);
        Size size2 = new Size(40.0, 40.0, 40.0);
        ComponentPlacementDTO placement1 = new ComponentPlacementDTO();
        placement1.setComponentType(ComponentType.CaseCooler);
        placement1.setMaxSize(size1);
        ComponentPlacementDTO placement2 = new ComponentPlacementDTO();
        placement2.setComponentType(ComponentType.CaseCooler);
        placement2.setMaxSize(size2);
        CaseCoolerDTO cooler1 = mock(CaseCoolerDTO.class);
        CaseCoolerDTO cooler2 = mock(CaseCoolerDTO.class);
        when(cooler1.getSize()).thenReturn(size1);
        when(cooler2.getSize()).thenReturn(size2);
        CaseDTO pcCase = mock(CaseDTO.class);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(placement1, placement2));
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        assertTrue(service.checkCaseCoolersAndCaseCompatibility(new CaseCoolerDTO[]{cooler1, cooler2}, pcCase, cpuCooler).isEmpty());
    }

    @Test
    void checkCaseCoolersAndCaseCompatibility_shouldReturnNonEmpty_whenNotAllFit() {
        CaseCoolerDTO cooler1 = mock(CaseCoolerDTO.class);
        CaseCoolerDTO cooler2 = mock(CaseCoolerDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        ComponentPlacementDTO placement1 = new ComponentPlacementDTO();
        Size size1 = new Size(40.0, 40.0, 40.0);
        Size size2 = new Size(60.0, 60.0, 60.0);
        when(cooler1.getSize()).thenReturn(size1);
        when(cooler2.getSize()).thenReturn(size2);
        placement1.setComponentType(ComponentType.CaseCooler);
        placement1.setMaxSize(size1);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(placement1));
        assertTrue(service.checkCaseCoolersAndCaseCompatibility(new CaseCoolerDTO[]{cooler1, cooler2}, pcCase, cpuCooler).isPresent());
    }

    @Test
    void isCPUCoolerCompatible_shouldReturnTrue_whenAirCoolerFitsAndCompatible() {
        PCDTO pc = mock(PCDTO.class);
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        CPUDTO cpu = mock(CPUDTO.class);
        MotherBoardDTO mb = mock(MotherBoardDTO.class);
        ComponentPlacementDTO placement = new ComponentPlacementDTO();
        placement.setComponentType(ComponentType.CPUCooler);
        Size coolerSize = new Size(40.0, 40.0, 40.0);
        Size maxSize = new Size(50.0, 50.0, 50.0);
        placement.setMaxSize(maxSize);
        when(cpuCooler.getCoolingType()).thenReturn(com.partcraft.back.enums.CoolingType.Air);
        when(pc.getPcCase()).thenReturn(pcCase);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(placement));
        when(cpuCooler.getSize()).thenReturn(coolerSize);
        when(cpuCooler.getMaxTDP()).thenReturn(100L);
        when(pc.getCpu()).thenReturn(cpu);
        when(cpu.getPowerDraw()).thenReturn(80);
        when(cpuCooler.getCpuSocket()).thenReturn("AM4");
        when(cpu.getSocketType()).thenReturn("AM4");
        when(pc.getMotherboard()).thenReturn(mb);
        assertTrue(service.isCPUCoolerCompatible(pc, cpuCooler));
    }

    @Test
    void isCPUCoolerCompatible_shouldThrowException_whenAirCoolerDoesNotFit() {
        PCDTO pc = mock(PCDTO.class);
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        ComponentPlacementDTO placement = new ComponentPlacementDTO();
        placement.setComponentType(ComponentType.CPUCooler);
        Size coolerSize = new Size(60.0, 60.0, 60.0);
        Size maxSize = new Size(50.0, 50.0, 50.0);
        placement.setMaxSize(maxSize);
        when(cpuCooler.getCoolingType()).thenReturn(com.partcraft.back.enums.CoolingType.Air);
        when(pc.getPcCase()).thenReturn(pcCase);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(placement));
        when(cpuCooler.getSize()).thenReturn(coolerSize);
        assertThatThrownBy(() -> service.isCPUCoolerCompatible(pc, cpuCooler))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("does not fit");
    }

    @Test
    void isCPUCoolerCompatible_shouldReturnTrue_whenLiquidCoolerFitsAndCompatible() {
        PCDTO pc = mock(PCDTO.class);
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        CPUDTO cpu = mock(CPUDTO.class);
        MotherBoardDTO mb = mock(MotherBoardDTO.class);
        // Setup placements for case coolers
        Size radiatorSize = new Size(40.0, 120.0, 40.0);
        ComponentPlacementDTO slot1 = new ComponentPlacementDTO();
        slot1.setComponentType(ComponentType.CaseCooler);
        slot1.setMaxSize(radiatorSize);
        slot1.setX(0.0);
        slot1.setY(0.0);
        slot1.setZ(0.0);
        ComponentPlacementDTO slot2 = new ComponentPlacementDTO();
        slot2.setComponentType(ComponentType.CaseCooler);
        slot2.setMaxSize(radiatorSize);
        slot2.setX(0.0);
        slot2.setY(0.0);
        slot2.setZ(1.0);
        when(pc.getPcCase()).thenReturn(pcCase);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(slot1, slot2));
        when(cpuCooler.getCoolingType()).thenReturn(com.partcraft.back.enums.CoolingType.Liquid);
        when(cpuCooler.getSize()).thenReturn(radiatorSize);
        when(cpuCooler.getCaseCoolerSlotsRequired()).thenReturn(2);
        when(pc.getCoolers()).thenReturn(Arrays.asList());
        when(pc.getMotherboard()).thenReturn(mb);
        when(mb.getSocketType()).thenReturn("AM4");
        when(cpuCooler.getCpuSocket()).thenReturn("AM4");
        when(pc.getCpu()).thenReturn(cpu);
        when(cpu.getPowerDraw()).thenReturn(80);
        when(cpuCooler.getMaxTDP()).thenReturn(100L);
        assertTrue(service.isCPUCoolerCompatible(pc, cpuCooler));
    }

    @Test
    void isCPUCoolerCompatible_shouldThrowException_whenLiquidCoolerNotEnoughSlots() {
        PCDTO pc = mock(PCDTO.class);
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        // Only one slot available
        Size radiatorSize = new Size(40.0, 120.0, 40.0);
        ComponentPlacementDTO slot1 = new ComponentPlacementDTO();
        slot1.setComponentType(ComponentType.CaseCooler);
        slot1.setMaxSize(radiatorSize);
        slot1.setX(0.0);
        slot1.setY(0.0);
        slot1.setZ(0.0);
        when(pc.getPcCase()).thenReturn(pcCase);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(slot1));
        when(cpuCooler.getCoolingType()).thenReturn(com.partcraft.back.enums.CoolingType.Liquid);
        when(cpuCooler.getSize()).thenReturn(radiatorSize);
        when(cpuCooler.getCaseCoolerSlotsRequired()).thenReturn(2);
        when(pc.getCoolers()).thenReturn(Arrays.asList());
        when(pc.getMotherboard()).thenReturn(mock(MotherBoardDTO.class));
        when(pc.getCpu()).thenReturn(mock(CPUDTO.class));
        when(cpuCooler.getMaxTDP()).thenReturn(100L);
        assertThatThrownBy(() -> service.isCPUCoolerCompatible(pc, cpuCooler))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("Not enough contiguous slots for radiator");
    }

    @Test
    void isCPUCoolerCompatible_shouldThrowException_whenLiquidCoolerSocketMismatch() {
        PCDTO pc = mock(PCDTO.class);
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        MotherBoardDTO mb = mock(MotherBoardDTO.class);
        Size radiatorSize = new Size(40.0, 120.0, 40.0);
        ComponentPlacementDTO slot1 = new ComponentPlacementDTO();
        slot1.setComponentType(ComponentType.CaseCooler);
        slot1.setMaxSize(radiatorSize);
        slot1.setX(0.0);
        slot1.setY(0.0);
        slot1.setZ(0.0);
        ComponentPlacementDTO slot2 = new ComponentPlacementDTO();
        slot2.setComponentType(ComponentType.CaseCooler);
        slot2.setMaxSize(radiatorSize);
        slot2.setX(0.0);
        slot2.setY(0.0);
        slot2.setZ(1.0);
        when(pc.getPcCase()).thenReturn(pcCase);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(slot1, slot2));
        when(cpuCooler.getCoolingType()).thenReturn(com.partcraft.back.enums.CoolingType.Liquid);
        when(cpuCooler.getSize()).thenReturn(radiatorSize);
        when(cpuCooler.getCaseCoolerSlotsRequired()).thenReturn(2);
        when(pc.getCoolers()).thenReturn(Arrays.asList());
        when(pc.getMotherboard()).thenReturn(mb);
        when(mb.getSocketType()).thenReturn("LGA1200");
        when(cpuCooler.getCpuSocket()).thenReturn("AM4");
        when(pc.getCpu()).thenReturn(mock(CPUDTO.class));
        when(cpuCooler.getMaxTDP()).thenReturn(100L);
        assertThatThrownBy(() -> service.isCPUCoolerCompatible(pc, cpuCooler))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("does not match motherboard socket");
    }

    @Test
    void isCPUCoolerCompatible_shouldThrowException_whenLiquidCoolerTDPInsufficient() {
        PCDTO pc = mock(PCDTO.class);
        CPUCoolerDTO cpuCooler = mock(CPUCoolerDTO.class);
        CaseDTO pcCase = mock(CaseDTO.class);
        CPUDTO cpu = mock(CPUDTO.class);
        MotherBoardDTO mb = mock(MotherBoardDTO.class);
        Size radiatorSize = new Size(40.0, 120.0, 40.0);
        ComponentPlacementDTO slot1 = new ComponentPlacementDTO();
        slot1.setComponentType(ComponentType.CaseCooler);
        slot1.setMaxSize(radiatorSize);
        slot1.setX(0.0);
        slot1.setY(0.0);
        slot1.setZ(0.0);
        ComponentPlacementDTO slot2 = new ComponentPlacementDTO();
        slot2.setComponentType(ComponentType.CaseCooler);
        slot2.setMaxSize(radiatorSize);
        slot2.setX(0.0);
        slot2.setY(0.0);
        slot2.setZ(1.0);
        when(pc.getPcCase()).thenReturn(pcCase);
        when(pcCase.getComponentPlacements()).thenReturn(List.of(slot1, slot2));
        when(cpuCooler.getCoolingType()).thenReturn(com.partcraft.back.enums.CoolingType.Liquid);
        when(cpuCooler.getSize()).thenReturn(radiatorSize);
        when(cpuCooler.getCaseCoolerSlotsRequired()).thenReturn(2);
        when(pc.getCoolers()).thenReturn(Arrays.asList());
        when(pc.getMotherboard()).thenReturn(mb);
        when(mb.getSocketType()).thenReturn("AM4");
        when(cpuCooler.getCpuSocket()).thenReturn("AM4");
        when(pc.getCpu()).thenReturn(cpu);
        when(cpu.getPowerDraw()).thenReturn(120);
        when(cpuCooler.getMaxTDP()).thenReturn(100L);
        assertThatThrownBy(() -> service.isCPUCoolerCompatible(pc, cpuCooler))
                .isInstanceOf(ComponentCompatibilityServiceException.class)
                .hasMessageContaining("insufficient for CPU power draw");
    }

    // Additional tests for liquid coolers, etc. would follow a similar pattern
}
