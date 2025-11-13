package com.partcraft.back.unit;

import com.partcraft.back.dto.ComponentPriceDTO;
import com.partcraft.back.entity.ComponentPrice;
import com.partcraft.back.repository.ComponentPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ComponentPriceServiceTest {
    @Mock
    private ComponentPriceRepository componentPriceRepository;
    @InjectMocks
    private com.partcraft.back.service.ComponentPriceService componentPriceService;

    private ComponentPrice sampleEntity;
    private ComponentPriceDTO sampleDTO;
    private LocalDate sampleDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleDate = LocalDate.of(2025, 11, 13);
        sampleEntity = new ComponentPrice();
        sampleEntity.setId(1L);
        sampleEntity.setValue(100.0);
        sampleEntity.setTime(sampleDate);
        sampleEntity.setLocation(com.partcraft.back.enums.Location.RU);
        sampleEntity.setComponentType("CPU");
        sampleEntity.setComponentId(100L);
        sampleDTO = new ComponentPriceDTO(sampleEntity);
    }

    @Test
    void getPrice_shouldReturnDTO_whenFound() {
        when(componentPriceRepository.findByComponentTypeAndComponentIdAndTime("CPU", 100L, sampleDate)).thenReturn(Optional.of(sampleEntity));
        ComponentPriceDTO result = componentPriceService.getPrice("CPU", 100L, sampleDate);
        assertThat(result).isEqualTo(sampleDTO);
        verify(componentPriceRepository, times(1)).findByComponentTypeAndComponentIdAndTime("CPU", 100L, sampleDate);
    }

    @Test
    void getPrice_shouldThrow_whenNotFound() {
        when(componentPriceRepository.findByComponentTypeAndComponentIdAndTime("CPU", 100L, sampleDate)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentPriceService.getPrice("CPU", 100L, sampleDate))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }

    @Test
    void getPriceHistory_shouldReturnListOfDTOs_whenFound() {
        List<ComponentPrice> entities = List.of(sampleEntity);
        when(componentPriceRepository.findAllByComponentTypeAndComponentId("CPU", 100L)).thenReturn(Optional.of(entities));
        List<ComponentPriceDTO> result = componentPriceService.getPriceHistory("CPU", 100L);
        assertThat(result).containsExactly(sampleDTO);
        verify(componentPriceRepository, times(1)).findAllByComponentTypeAndComponentId("CPU", 100L);
    }

    @Test
    void getPriceHistory_shouldThrow_whenNotFound() {
        when(componentPriceRepository.findAllByComponentTypeAndComponentId("CPU", 100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentPriceService.getPriceHistory("CPU", 100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }

    @Test
    void getPriceHistory_shouldThrow_whenEmpty() {
        when(componentPriceRepository.findAllByComponentTypeAndComponentId("CPU", 100L)).thenReturn(Optional.of(List.of()));
        assertThatThrownBy(() -> componentPriceService.getPriceHistory("CPU", 100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }

    @Test
    void addComponentPrice_shouldSaveEntity() {
        doAnswer(invocation -> {
            ComponentPrice price = invocation.getArgument(0);
            assertThat(price.getValue()).isEqualTo(sampleDTO.getValue());
            assertThat(price.getTime()).isEqualTo(sampleDTO.getTime());
            assertThat(price.getLocation()).isEqualTo(sampleDTO.getLocation());
            assertThat(price.getComponentType()).isEqualTo(sampleDTO.getComponentType());
            assertThat(price.getComponentId()).isEqualTo(sampleDTO.getComponentId());
            return price;
        }).when(componentPriceRepository).save(any(ComponentPrice.class));
        componentPriceService.addComponentPrice(sampleDTO);
        verify(componentPriceRepository, times(1)).save(any(ComponentPrice.class));
    }

    @Test
    void updateComponentPrice_shouldUpdate_whenFound() {
        ComponentPriceDTO updatedDTO = new ComponentPriceDTO(200.0, sampleDate, com.partcraft.back.enums.Location.NL, "CPU", 100L);
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        componentPriceService.updateComponentPrice(1L, updatedDTO);
        assertThat(sampleEntity.getValue()).isEqualTo(200.0);
        assertThat(sampleEntity.getLocation()).isEqualTo(com.partcraft.back.enums.Location.NL);
        verify(componentPriceRepository, times(1)).findById(1L);
        verify(componentPriceRepository, times(1)).save(sampleEntity);
    }

    @Test
    void updateComponentPrice_shouldThrow_whenNotFound() {
        ComponentPriceDTO updatedDTO = new ComponentPriceDTO(200.0, sampleDate, com.partcraft.back.enums.Location.NL, "CPU", 100L);
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentPriceService.updateComponentPrice(1L, updatedDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }

    @Test
    void addComponentPrice_shouldHandleNullDTO() {
        assertThatThrownBy(() -> componentPriceService.addComponentPrice(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void updateComponentPrice_shouldHandleNullDTO() {
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        assertThatThrownBy(() -> componentPriceService.updateComponentPrice(1L, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void updateComponentPrice_shouldHandleRepositoryException() {
        ComponentPriceDTO updatedDTO = new ComponentPriceDTO(200.0, sampleDate, com.partcraft.back.enums.Location.NL, "CPU", 100L);
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        doThrow(new RuntimeException("Update error")).when(componentPriceRepository).save(any(ComponentPrice.class));
        assertThatThrownBy(() -> componentPriceService.updateComponentPrice(1L, updatedDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Update error");
    }
}
