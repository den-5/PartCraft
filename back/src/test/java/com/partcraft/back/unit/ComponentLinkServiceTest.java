package com.partcraft.back.unit;

import com.partcraft.back.dto.ComponentLinkDTO;
import com.partcraft.back.entity.ComponentLink;
import com.partcraft.back.repository.ComponentLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import com.partcraft.back.service.ComponentLinkService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ComponentLinkServiceTest {
    @Mock
    private ComponentLinkRepository componentLinkRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ComponentLinkService componentLinkService;

    private ComponentLinkDTO sampleDTO;
    private ComponentLink sampleEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleDTO = new ComponentLinkDTO(1L, "CPU", 100L, "http://example.com");
        sampleEntity = new ComponentLink();
        sampleEntity.setId(1L);
        sampleEntity.setComponentType("CPU");
        sampleEntity.setComponentId(100L);
        sampleEntity.setUrl("http://example.com");
    }

    @Test
    void createComponentLink_shouldSaveAndReturnDTO() {
        when(modelMapper.map(sampleDTO, ComponentLink.class)).thenReturn(sampleEntity);
        when(componentLinkRepository.save(sampleEntity)).thenReturn(sampleEntity);
        when(modelMapper.map(sampleEntity, ComponentLinkDTO.class)).thenReturn(sampleDTO);

        ComponentLinkDTO result = componentLinkService.createComponentLink(sampleDTO);
        assertThat(result).isEqualTo(sampleDTO);
        verify(componentLinkRepository, times(1)).save(sampleEntity);
    }

    @Test
    void getComponentLink_shouldReturnDTO_whenFound() {
        when(componentLinkRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(modelMapper.map(sampleEntity, ComponentLinkDTO.class)).thenReturn(sampleDTO);

        ComponentLinkDTO result = componentLinkService.getComponentLink(1L);
        assertThat(result).isEqualTo(sampleDTO);
        verify(componentLinkRepository, times(1)).findById(1L);
    }

    @Test
    void getComponentLink_shouldThrow_whenNotFound() {
        when(componentLinkRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentLinkService.getComponentLink(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ComponentLink not found");
    }

    @Test
    void getAllComponentLinks_shouldReturnListOfDTOs() {
        ComponentLink entity2 = new ComponentLink();
        entity2.setId(2L);
        entity2.setComponentType("GPU");
        entity2.setComponentId(200L);
        entity2.setUrl("http://gpu.com");
        ComponentLinkDTO dto2 = new ComponentLinkDTO(2L, "GPU", 200L, "http://gpu.com");

        List<ComponentLink> entities = Arrays.asList(sampleEntity, entity2);
        List<ComponentLinkDTO> dtos = Arrays.asList(sampleDTO, dto2);

        when(componentLinkRepository.findAll()).thenReturn(entities);
        when(modelMapper.map(sampleEntity, ComponentLinkDTO.class)).thenReturn(sampleDTO);
        when(modelMapper.map(entity2, ComponentLinkDTO.class)).thenReturn(dto2);

        List<ComponentLinkDTO> result = componentLinkService.getAllComponentLinks();
        assertThat(result).containsExactlyElementsOf(dtos);
        verify(componentLinkRepository, times(1)).findAll();
    }

    @Test
    void updateComponentLink_shouldUpdateAndReturnDTO_whenFound() {
        ComponentLinkDTO updatedDTO = new ComponentLinkDTO(1L, "GPU", 200L, "http://gpu.com");
        ComponentLink updatedEntity = new ComponentLink();
        updatedEntity.setId(1L);
        updatedEntity.setComponentType("GPU");
        updatedEntity.setComponentId(200L);
        updatedEntity.setUrl("http://gpu.com");

        when(componentLinkRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(componentLinkRepository.save(any(ComponentLink.class))).thenReturn(updatedEntity);
        when(modelMapper.map(updatedEntity, ComponentLinkDTO.class)).thenReturn(updatedDTO);

        ComponentLinkDTO result = componentLinkService.updateComponentLink(1L, updatedDTO);
        assertThat(result).isEqualTo(updatedDTO);
        verify(componentLinkRepository, times(1)).findById(1L);
        verify(componentLinkRepository, times(1)).save(any(ComponentLink.class));
    }

    @Test
    void updateComponentLink_shouldThrow_whenNotFound() {
        when(componentLinkRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> componentLinkService.updateComponentLink(1L, sampleDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ComponentLink not found");
    }

    @Test
    void deleteComponentLink_shouldDelete_whenExists() {
        when(componentLinkRepository.existsById(1L)).thenReturn(true);
        componentLinkService.deleteComponentLink(1L);
        verify(componentLinkRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteComponentLink_shouldThrow_whenNotExists() {
        when(componentLinkRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> componentLinkService.deleteComponentLink(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ComponentLink not found");
    }
}

