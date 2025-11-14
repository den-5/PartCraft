package com.partcraft.back.unit;

import com.partcraft.back.dto.ComponentPriceDTO;
import com.partcraft.back.entity.ComponentPrice;
import com.partcraft.back.repository.ComponentPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ComponentPriceServiceTest {
    @Mock
    private ComponentPriceRepository componentPriceRepository;
    @Mock
    private ModelMapper modelMapper;
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
        sampleDTO = new ComponentPriceDTO();
        sampleDTO.setId(1L);
        sampleDTO.setValue(100.0);
        sampleDTO.setTime(sampleDate);
        sampleDTO.setLocation(com.partcraft.back.enums.Location.RU);
        sampleDTO.setComponentType("CPU");
        sampleDTO.setComponentId(100L);

        // Mock modelMapper.map(entity, dto) for void mapping used in streams
        doAnswer(invocation -> {
            ComponentPrice src = invocation.getArgument(0);
            ComponentPriceDTO dest = invocation.getArgument(1);
            dest.setId(src.getId());
            dest.setValue(src.getValue());
            dest.setTime(src.getTime());
            dest.setLocation(src.getLocation());
            dest.setComponentType(src.getComponentType());
            dest.setComponentId(src.getComponentId());
            return null;
        }).when(modelMapper).map(any(ComponentPrice.class), any(ComponentPriceDTO.class));
    }

    @Test
    void getComponentPrice_shouldReturnDTO_whenFound() {
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        ComponentPriceDTO result = componentPriceService.getComponentPrice(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getValue()).isEqualTo(100.0);
        assertThat(result.getTime()).isEqualTo(sampleDate);
        assertThat(result.getLocation()).isEqualTo(com.partcraft.back.enums.Location.RU);
        assertThat(result.getComponentType()).isEqualTo("CPU");
        assertThat(result.getComponentId()).isEqualTo(100L);

        verify(componentPriceRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(any(ComponentPrice.class), any(ComponentPriceDTO.class));
    }

    @Test
    void getComponentPrice_shouldThrow_whenNotFound() {
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentPriceService.getComponentPrice(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }

    @Test
    void getAllComponentPrices_shouldReturnListOfDTOs_whenFound() {
        List<ComponentPrice> entities = List.of(sampleEntity);
        when(componentPriceRepository.findAllByComponentTypeAndComponentId("CPU", 100L)).thenReturn(Optional.of(entities));

        List<ComponentPriceDTO> result = componentPriceService.getAllComponentPrices(100L, "CPU");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getValue()).isEqualTo(100.0);
        assertThat(result.get(0).getTime()).isEqualTo(sampleDate);
        assertThat(result.get(0).getLocation()).isEqualTo(com.partcraft.back.enums.Location.RU);
        assertThat(result.get(0).getComponentType()).isEqualTo("CPU");
        assertThat(result.get(0).getComponentId()).isEqualTo(100L);

        verify(componentPriceRepository, times(1)).findAllByComponentTypeAndComponentId("CPU", 100L);
        verify(modelMapper, times(1)).map(any(ComponentPrice.class), any(ComponentPriceDTO.class));
    }

    @Test
    void getAllComponentPrices_shouldThrow_whenNotFound() {
        when(componentPriceRepository.findAllByComponentTypeAndComponentId("CPU", 100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentPriceService.getAllComponentPrices(100L, "CPU"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }

    @Test
    void getAllComponentPrices_shouldThrow_whenEmpty() {
        when(componentPriceRepository.findAllByComponentTypeAndComponentId("CPU", 100L)).thenReturn(Optional.of(List.of()));
        assertThatThrownBy(() -> componentPriceService.getAllComponentPrices(100L, "CPU"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }

    @Test
    void createComponentPrice_shouldSaveEntity() {
        // Mock DTO to Entity mapping
        doAnswer(invocation -> {
            ComponentPriceDTO src = invocation.getArgument(0);
            ComponentPrice dest = invocation.getArgument(1);
            dest.setValue(src.getValue());
            dest.setTime(src.getTime());
            dest.setLocation(src.getLocation());
            dest.setComponentType(src.getComponentType());
            dest.setComponentId(src.getComponentId());
            return null;
        }).when(modelMapper).map(eq(sampleDTO), any(ComponentPrice.class));

        when(componentPriceRepository.save(any(ComponentPrice.class))).thenAnswer(invocation -> {
            ComponentPrice price = invocation.getArgument(0);
            price.setId(1L);
            return price;
        });

        ComponentPriceDTO result = componentPriceService.createComponentPrice(sampleDTO);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getValue()).isEqualTo(sampleDTO.getValue());
        assertThat(result.getTime()).isEqualTo(sampleDTO.getTime());
        assertThat(result.getLocation()).isEqualTo(sampleDTO.getLocation());
        assertThat(result.getComponentType()).isEqualTo(sampleDTO.getComponentType());
        assertThat(result.getComponentId()).isEqualTo(sampleDTO.getComponentId());

        verify(componentPriceRepository, times(1)).save(any(ComponentPrice.class));
        verify(modelMapper, times(1)).map(eq(sampleDTO), any(ComponentPrice.class));
        verify(modelMapper, times(1)).map(any(ComponentPrice.class), any(ComponentPriceDTO.class));
    }

    @Test
    void updateComponentPrice_shouldUpdate_whenFound() {
        ComponentPriceDTO updatedDTO = new ComponentPriceDTO();
        updatedDTO.setValue(200.0);
        updatedDTO.setTime(sampleDate);
        updatedDTO.setLocation(com.partcraft.back.enums.Location.NL);
        updatedDTO.setComponentType("CPU");
        updatedDTO.setComponentId(100L);

        when(componentPriceRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        doAnswer(invocation -> {
            ComponentPriceDTO dto = invocation.getArgument(0);
            ComponentPrice entity = invocation.getArgument(1);
            entity.setValue(dto.getValue());
            entity.setLocation(dto.getLocation());
            return null;
        }).when(modelMapper).map(eq(updatedDTO), eq(sampleEntity));

        when(componentPriceRepository.save(any(ComponentPrice.class))).thenReturn(sampleEntity);

        ComponentPriceDTO result = componentPriceService.updateComponentPrice(1L, updatedDTO);

        assertThat(result.getValue()).isEqualTo(200.0);
        assertThat(result.getLocation()).isEqualTo(com.partcraft.back.enums.Location.NL);

        verify(componentPriceRepository, times(1)).findById(1L);
        verify(componentPriceRepository, times(1)).save(sampleEntity);
        verify(modelMapper, times(1)).map(eq(updatedDTO), eq(sampleEntity));
        verify(modelMapper, times(1)).map(any(ComponentPrice.class), any(ComponentPriceDTO.class));
    }

    @Test
    void updateComponentPrice_shouldThrow_whenNotFound() {
        ComponentPriceDTO updatedDTO = new ComponentPriceDTO();
        updatedDTO.setValue(200.0);
        updatedDTO.setTime(sampleDate);
        updatedDTO.setLocation(com.partcraft.back.enums.Location.NL);
        updatedDTO.setComponentType("CPU");
        updatedDTO.setComponentId(100L);
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentPriceService.updateComponentPrice(1L, updatedDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }


    @Test
    void updateComponentPrice_shouldHandleRepositoryException() {
        ComponentPriceDTO updatedDTO = new ComponentPriceDTO();
        updatedDTO.setValue(200.0);
        updatedDTO.setTime(sampleDate);
        updatedDTO.setLocation(com.partcraft.back.enums.Location.NL);
        updatedDTO.setComponentType("CPU");
        updatedDTO.setComponentId(100L);
        when(componentPriceRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        doAnswer(invocation -> {
            ComponentPriceDTO dto = invocation.getArgument(0);
            sampleEntity.setValue(dto.getValue());
            sampleEntity.setLocation(dto.getLocation());
            return null;
        }).when(modelMapper).map(updatedDTO, sampleEntity);
        doThrow(new RuntimeException("Update error")).when(componentPriceRepository).save(any(ComponentPrice.class));
        assertThatThrownBy(() -> componentPriceService.updateComponentPrice(1L, updatedDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Update error");
    }

    @Test
    void deleteComponentPrice_shouldDelete_whenExists() {
        when(componentPriceRepository.existsById(1L)).thenReturn(true);
        componentPriceService.deleteComponentPrice(1L);
        verify(componentPriceRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteComponentPrice_shouldThrow_whenNotExists() {
        when(componentPriceRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> componentPriceService.deleteComponentPrice(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Price for this component not found");
    }
}
