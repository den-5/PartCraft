package com.partcraft.back.service;

import com.partcraft.back.dto.ComponentLinkDTO;
import com.partcraft.back.entity.ComponentLink;
import com.partcraft.back.repository.ComponentLinkRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComponentLinkService {
    private final ComponentLinkRepository componentLinkRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ComponentLinkService(ComponentLinkRepository componentLinkRepository, ModelMapper modelMapper) {
        this.componentLinkRepository = componentLinkRepository;
        this.modelMapper = modelMapper;
    }

    public ComponentLinkDTO createComponentLink(ComponentLinkDTO dto) {
        ComponentLink link = modelMapper.map(dto, ComponentLink.class);
        ComponentLink saved = componentLinkRepository.save(link);
        return modelMapper.map(saved, ComponentLinkDTO.class);
    }

    public ComponentLinkDTO getComponentLink(Long id) {
        ComponentLink link = componentLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ComponentLink not found"));
        return modelMapper.map(link, ComponentLinkDTO.class);
    }

    public List<ComponentLinkDTO> getAllComponentLinks() {
        return componentLinkRepository.findAll().stream()
                .map(link -> modelMapper.map(link, ComponentLinkDTO.class))
                .collect(Collectors.toList());
    }

    public ComponentLinkDTO updateComponentLink(Long id, ComponentLinkDTO dto) {
        ComponentLink link = componentLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ComponentLink not found"));
        link.setComponentType(dto.getComponentType());
        link.setComponentId(dto.getComponentId());
        link.setUrl(dto.getUrl());
        ComponentLink updated = componentLinkRepository.save(link);
        return modelMapper.map(updated, ComponentLinkDTO.class);
    }

    public void deleteComponentLink(Long id) {
        if (!componentLinkRepository.existsById(id)) {
            throw new RuntimeException("ComponentLink not found");
        }
        componentLinkRepository.deleteById(id);
    }
}
