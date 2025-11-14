package com.partcraft.back.unit;

import com.partcraft.back.dto.componentDTO.CPUDTO;
import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.CPU;
import com.partcraft.back.exception.service.ComponentServiceException;
import com.partcraft.back.repository.component.CPURepository;
import com.partcraft.back.service.component.CPUService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ComponentServiceTest {
    private CPURepository cpuRepository;
    private CPUService cpuService;

    @BeforeEach
    void setUp() throws Exception {
        cpuRepository = mock(CPURepository.class);
        cpuService = new CPUService();
        java.lang.reflect.Field field = CPUService.class.getDeclaredField("cpuRepository");
        field.setAccessible(true);
        field.set(cpuService, cpuRepository);
    }

    @Test
    void create_shouldSaveAndReturnDTO() {
        CPUDTO dto = sampleCPUDTO();
        CPU cpu = new CPU(dto);
        cpu.setId(1L);

        when(cpuRepository.save(any(CPU.class))).thenReturn(cpu);

        CPUDTO result = cpuService.create(dto);

        assertThat(result.getCpuModel()).isEqualTo(dto.getCpuModel());
        assertThat(result.getId()).isEqualTo(1L);
        verify(cpuRepository, times(1)).save(any(CPU.class));
    }

    @Test
    void create_shouldThrowException_whenRepositoryFails() {
        CPUDTO dto = sampleCPUDTO();
        when(cpuRepository.save(any(CPU.class))).thenThrow(new RuntimeException("DB error"));
        assertThatThrownBy(() -> cpuService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");
    }

    @Test
    void findById_shouldReturnDTO() {
        CPU cpu = new CPU(sampleCPUDTO());
        cpu.setId(1L);

        when(cpuRepository.findById(1L)).thenReturn(Optional.of(cpu));

        CPUDTO result = cpuService.findById(1L);

        assertThat(result.getCpuModel()).isEqualTo(cpu.getCpuModel());
        assertThat(result.getId()).isEqualTo(1L);
        verify(cpuRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenEntityDoesNotExist() {
        when(cpuRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cpuService.findById(42L))
                .isInstanceOf(ComponentServiceException.class)
                .hasMessageContaining("Entity with id 42 not found");
    }

    @Test
    void findAll_shouldReturnListOfDTOs() {
        CPU cpu1 = new CPU(sampleCPUDTO());
        cpu1.setId(1L);
        CPU cpu2 = new CPU(sampleCPUDTO());
        cpu2.setCpuModel("AMD Ryzen 9 7950X");
        cpu2.setId(2L);

        when(cpuRepository.findAll()).thenReturn(List.of(cpu1, cpu2));

        List<CPUDTO> result = cpuService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCpuModel()).isEqualTo("Intel i9-13900K");
        assertThat(result.get(1).getCpuModel()).isEqualTo("AMD Ryzen 9 7950X");
        verify(cpuRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEntitiesExist() {
        when(cpuRepository.findAll()).thenReturn(List.of());
        List<CPUDTO> result = cpuService.findAll();
        assertThat(result).isEmpty();
        verify(cpuRepository, times(1)).findAll();
    }

    @Test
    void update_shouldUpdateAndReturnDTO() {
        CPUDTO dto = sampleCPUDTO();
        CPU cpu = new CPU(dto);
        cpu.setId(1L);

        when(cpuRepository.existsById(1L)).thenReturn(true);
        when(cpuRepository.save(any(CPU.class))).thenReturn(cpu);

        dto.setId(1L); // Ensure DTO has the same ID as the entity
        dto.setCpuModel("Intel i9-14900K");
        cpu.setCpuModel("Intel i9-14900K");
        CPUDTO result = cpuService.update(1L, dto);

        assertThat(result.getCpuModel()).isEqualTo("Intel i9-14900K");
        assertThat(result.getId()).isEqualTo(1L);
        verify(cpuRepository, times(1)).existsById(1L);
        verify(cpuRepository, times(1)).save(any(CPU.class));
    }

    @Test
    void update_shouldThrowException_whenRepositoryFails() {
        CPUDTO dto = sampleCPUDTO();
        when(cpuRepository.existsById(1L)).thenReturn(true);
        when(cpuRepository.save(any(CPU.class))).thenThrow(new RuntimeException("DB error"));
        dto.setId(1L);
        assertThatThrownBy(() -> cpuService.update(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");
    }

    @Test
    void update_shouldThrowException_whenEntityDoesNotExist() {
        CPUDTO dto = sampleCPUDTO();
        when(cpuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cpuService.update(99L, dto))
                .isInstanceOf(ComponentServiceException.class)
                .hasMessageContaining("Entity with id 99 not found");
    }

    @Test
    void delete_shouldCallRepositoryDelete_whenEntityExists() {
        when(cpuRepository.existsById(1L)).thenReturn(true);
        cpuService.delete(1L);
        verify(cpuRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenRepositoryFails() {
        when(cpuRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(cpuRepository).deleteById(1L);
        assertThatThrownBy(() -> cpuService.delete(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");
    }

    @Test
    void delete_shouldNotCallRepositoryDelete_whenEntityDoesNotExist() {
        when(cpuRepository.existsById(2L)).thenReturn(false);
        assertThatThrownBy(() -> cpuService.delete(2L)).isInstanceOf(ComponentServiceException.class);
        verify(cpuRepository, never()).deleteById(2L);
    }

    private CPUDTO sampleCPUDTO() {
        Size size = new Size(37.5, 45.0, 7.5);
        CPUDTO dto = new CPUDTO();
        dto.setCpuBrand("Intel");
        dto.setCpuModel("Intel i9-13900K");
        dto.setCpuCores(24);
        dto.setCpuThreads(32);
        dto.setCpuBaseClockGhz(3.0);
        dto.setCpuBoostClockGhz(5.8);
        dto.setPictureUrl("https://example.com/cpu.jpg");
        dto.setSize(size);
        dto.setPowerDraw(253);
        return dto;
    }
}
