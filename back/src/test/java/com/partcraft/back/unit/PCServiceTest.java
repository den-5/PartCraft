package com.partcraft.back.unit;

import com.partcraft.back.dto.PC.CreatePCDTO;
import com.partcraft.back.dto.PC.PCDTO;
import com.partcraft.back.entity.PC;
import com.partcraft.back.entity.User;
import com.partcraft.back.entity.component.CPU;
import com.partcraft.back.exception.ComponentCompatibilityServiceException;
import com.partcraft.back.exception.PCServiceException;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.repository.PCRepository;
import com.partcraft.back.service.ComponentCompatibilityService;
import com.partcraft.back.service.PCService;
import com.partcraft.back.service.UserService;
import com.partcraft.back.service.helper.ComponentRepositoryManager;
import com.partcraft.back.service.helper.SetPCComponentsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PCServiceTest {
    private PCRepository pcRepository;
    private UserService userService;
    private UserRepository userRepository;
    private ComponentRepositoryManager components;
    private PCService pcService;
    private SetPCComponentsManager setPCComponentsManager;
    private ComponentCompatibilityService compatibilityService;

    @BeforeEach
    void setUp() {
        pcRepository = mock(PCRepository.class);
        userService = mock(UserService.class);
        userRepository = mock(UserRepository.class);
        components = mock(ComponentRepositoryManager.class);
        setPCComponentsManager = new SetPCComponentsManager(components);
        compatibilityService = mock(ComponentCompatibilityService.class);
        pcService = new PCService(pcRepository, userService, userRepository, components, setPCComponentsManager, compatibilityService);
    }

    @Test
    void testCreatePC_cpuNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setCpuId(2L);
        var cpuRepo = mock(com.partcraft.back.repository.component.CPURepository.class);
        when(components.getCpuRepository()).thenReturn(cpuRepo);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(cpuRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_minimalFields() {
        CreatePCDTO dto = createMockPCDTO();
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(pcRepository.save(any(PC.class))).thenAnswer(inv -> inv.getArgument(0));
        PCDTO result = pcService.createPC(dto, "user");
        assertEquals("Test PC", result.getName());
        assertNull(result.getCpu());
    }

    @Test
    void testCreatePC_allComponentsPresent() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setCpuId(1L);
        dto.setGpuId(2L);
        dto.setRamKitId(3L);
        dto.setStorageId(4L);
        dto.setPsuId(5L);
        dto.setCpuCoolerId(6L);
        dto.setMotherboardId(7L);
        dto.setPcCaseId(8L);
        dto.setCoolerIds(java.util.List.of(9L, 10L));
        User user = new User();
        user.setId(11L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var cpu = new CPU();
        cpu.setId(1L);
        var gpu = new com.partcraft.back.entity.component.GPU();
        gpu.setId(2L);
        var ramKit = new com.partcraft.back.entity.component.RAMKit();
        ramKit.setId(3L);
        var storage = new com.partcraft.back.entity.component.Storage();
        storage.setId(4L);
        var psu = new com.partcraft.back.entity.component.PSU();
        psu.setId(5L);
        var cpuCooler = new com.partcraft.back.entity.component.CPUCooler();
        cpuCooler.setId(6L);
        var mb = new com.partcraft.back.entity.component.MotherBoard();
        mb.setId(7L);
        var pcCase = new com.partcraft.back.entity.component.Case();
        pcCase.setId(8L);
        var cooler1 = new com.partcraft.back.entity.component.CaseCooler();
        cooler1.setId(9L);
        var cooler2 = new com.partcraft.back.entity.component.CaseCooler();
        cooler2.setId(10L);
        var cpuRepo = mock(com.partcraft.back.repository.component.CPURepository.class);
        var gpuRepo = mock(com.partcraft.back.repository.component.GPURepository.class);
        var ramKitRepo = mock(com.partcraft.back.repository.component.RAMKitRepository.class);
        var storageRepo = mock(com.partcraft.back.repository.component.StorageRepository.class);
        var psuRepo = mock(com.partcraft.back.repository.component.PSURepository.class);
        var cpuCoolerRepo = mock(com.partcraft.back.repository.component.CPUCoolerRepository.class);
        var mbRepo = mock(com.partcraft.back.repository.component.MotherBoardRepository.class);
        var caseRepo = mock(com.partcraft.back.repository.component.CaseRepository.class);
        var caseCoolerRepo = mock(com.partcraft.back.repository.component.CaseCoolerRepository.class);
        when(cpuRepo.findById(1L)).thenReturn(Optional.of(cpu));
        when(gpuRepo.findById(2L)).thenReturn(Optional.of(gpu));
        when(ramKitRepo.findById(3L)).thenReturn(Optional.of(ramKit));
        when(storageRepo.findById(4L)).thenReturn(Optional.of(storage));
        when(psuRepo.findById(5L)).thenReturn(Optional.of(psu));
        when(cpuCoolerRepo.findById(6L)).thenReturn(Optional.of(cpuCooler));
        when(mbRepo.findById(7L)).thenReturn(Optional.of(mb));
        when(caseRepo.findById(8L)).thenReturn(Optional.of(pcCase));
        when(caseCoolerRepo.findById(9L)).thenReturn(Optional.of(cooler1));
        when(caseCoolerRepo.findById(10L)).thenReturn(Optional.of(cooler2));
        when(components.getCpuRepository()).thenReturn(cpuRepo);
        when(components.getGpuRepository()).thenReturn(gpuRepo);
        when(components.getRamKitRepository()).thenReturn(ramKitRepo);
        when(components.getStorageRepository()).thenReturn(storageRepo);
        when(components.getPsuRepository()).thenReturn(psuRepo);
        when(components.getCpuCoolerRepository()).thenReturn(cpuCoolerRepo);
        when(components.getMotherBoardRepository()).thenReturn(mbRepo);
        when(components.getCaseRepository()).thenReturn(caseRepo);
        when(components.getCaseCoolerRepository()).thenReturn(caseCoolerRepo);
        when(pcRepository.save(any(PC.class))).thenAnswer(inv -> inv.getArgument(0));
        PCDTO result = pcService.createPC(dto, "user");
        assertEquals("Test PC", result.getName());
        assertNotNull(result.getCpu());
        assertNotNull(result.getGpu());
        assertNotNull(result.getRamKit());
        assertNotNull(result.getStorage());
        assertNotNull(result.getPsu());
        assertNotNull(result.getCpuCooler());
        assertNotNull(result.getMotherboard());
        assertNotNull(result.getPcCase());
        assertNotNull(result.getCoolers());
        assertEquals(2, result.getCoolers().size());
    }

    @Test
    void testCreatePC_gpuNotFound_throwsException() {
        CreatePCDTO dto = new CreatePCDTO();
        dto.setName("Test PC");
        dto.setDescription("desc");
        dto.setPurpose("gaming");
        dto.setGpuId(2L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var gpuRepo = mock(com.partcraft.back.repository.component.GPURepository.class);
        when(gpuRepo.findById(2L)).thenReturn(Optional.empty());
        when(components.getGpuRepository()).thenReturn(gpuRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_ramKitNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setRamKitId(3L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var ramKitRepo = mock(com.partcraft.back.repository.component.RAMKitRepository.class);
        when(ramKitRepo.findById(3L)).thenReturn(Optional.empty());
        when(components.getRamKitRepository()).thenReturn(ramKitRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_storageNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setStorageId(4L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var storageRepo = mock(com.partcraft.back.repository.component.StorageRepository.class);
        when(storageRepo.findById(4L)).thenReturn(Optional.empty());
        when(components.getStorageRepository()).thenReturn(storageRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_psuNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setPsuId(5L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var psuRepo = mock(com.partcraft.back.repository.component.PSURepository.class);
        when(psuRepo.findById(5L)).thenReturn(Optional.empty());
        when(components.getPsuRepository()).thenReturn(psuRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_cpuCoolerNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setCpuCoolerId(6L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var cpuCoolerRepo = mock(com.partcraft.back.repository.component.CPUCoolerRepository.class);
        when(cpuCoolerRepo.findById(6L)).thenReturn(Optional.empty());
        when(components.getCpuCoolerRepository()).thenReturn(cpuCoolerRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_motherboardNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setMotherboardId(7L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var mbRepo = mock(com.partcraft.back.repository.component.MotherBoardRepository.class);
        when(mbRepo.findById(7L)).thenReturn(Optional.empty());
        when(components.getMotherBoardRepository()).thenReturn(mbRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_caseNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setPcCaseId(8L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var caseRepo = mock(com.partcraft.back.repository.component.CaseRepository.class);
        when(caseRepo.findById(8L)).thenReturn(Optional.empty());
        when(components.getCaseRepository()).thenReturn(caseRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_coolerNotFound_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setCoolerIds(java.util.List.of(9L));
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var caseCoolerRepo = mock(com.partcraft.back.repository.component.CaseCoolerRepository.class);
        when(caseCoolerRepo.findById(9L)).thenReturn(Optional.empty());
        when(components.getCaseCoolerRepository()).thenReturn(caseCoolerRepo);
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_incompatibleCpuMotherboard_throwsException() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setCpuId(1L);
        dto.setMotherboardId(2L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var cpuRepo = mock(com.partcraft.back.repository.component.CPURepository.class);
        var mbRepo = mock(com.partcraft.back.repository.component.MotherBoardRepository.class);
        var cpu = new com.partcraft.back.entity.component.CPU();
        cpu.setId(1L);
        var mb = new com.partcraft.back.entity.component.MotherBoard();
        mb.setId(2L);
        when(cpuRepo.findById(1L)).thenReturn(Optional.of(cpu));
        when(mbRepo.findById(2L)).thenReturn(Optional.of(mb));
        when(components.getCpuRepository()).thenReturn(cpuRepo);
        when(components.getMotherBoardRepository()).thenReturn(mbRepo);
        doThrow(new ComponentCompatibilityServiceException("CPU and motherboard sockets do not match")).when(compatibilityService)
                .isCpuAndMotherboardCompatible(any(), any());
        assertThrows(PCServiceException.class, () -> pcService.createPC(dto, "user"));
    }

    @Test
    void testCreatePC_compatibleCpuMotherboard_succeeds() {
        CreatePCDTO dto = createMockPCDTO();
        dto.setCpuId(1L);
        dto.setMotherboardId(2L);
        User user = new User();
        user.setId(10L);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        var cpuRepo = mock(com.partcraft.back.repository.component.CPURepository.class);
        var mbRepo = mock(com.partcraft.back.repository.component.MotherBoardRepository.class);
        var cpu = new com.partcraft.back.entity.component.CPU();
        cpu.setId(1L);
        var mb = new com.partcraft.back.entity.component.MotherBoard();
        mb.setId(2L);
        when(cpuRepo.findById(1L)).thenReturn(Optional.of(cpu));
        when(mbRepo.findById(2L)).thenReturn(Optional.of(mb));
        when(components.getCpuRepository()).thenReturn(cpuRepo);
        when(components.getMotherBoardRepository()).thenReturn(mbRepo);
        when(compatibilityService.isCpuAndMotherboardCompatible(any(), any())).thenReturn(true);
        when(pcRepository.save(any(PC.class))).thenAnswer(inv -> inv.getArgument(0));
        PCDTO result = pcService.createPC(dto, "user");
        assertEquals("Test PC", result.getName());
    }

    private CreatePCDTO createMockPCDTO() {
        CreatePCDTO dto = new CreatePCDTO();
        dto.setName("Test PC");
        dto.setDescription("desc");
        dto.setPurpose("gaming");
        return dto;
    }
}
