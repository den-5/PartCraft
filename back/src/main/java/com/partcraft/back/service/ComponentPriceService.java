package com.partcraft.back.service;

import com.partcraft.back.dto.ComponentPriceDTO;
import com.partcraft.back.entity.ComponentPrice;
import com.partcraft.back.exception.NotFoundException;
import com.partcraft.back.repository.ComponentPriceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentPriceService {

    @Autowired
    private ComponentPriceRepository componentPriceRepository;

    @Autowired
    private ModelMapper modelMapper;


    // Fetch a single price by id
    public ComponentPriceDTO getComponentPrice(Long id) {
        var price = componentPriceRepository.findById(id).orElse(null);
        if (price == null) {
            throw new NotFoundException("Price for this component not found");
        }
        var componentPriceDTO = new ComponentPriceDTO();
        modelMapper.map(price, componentPriceDTO);
        return componentPriceDTO;
    }

    // Fetch all prices for a component (history)
    public List<ComponentPriceDTO> getAllComponentPrices(Long componentId, String componentType) {
        var prices = componentPriceRepository.findAllByComponentTypeAndComponentId(componentType, componentId).orElse(null);
        if (prices == null || prices.isEmpty()) {
            throw new NotFoundException("Price for this component not found");
        }
        return prices.stream().map(price -> {
            var dto = new ComponentPriceDTO();
            modelMapper.map(price, dto);
            return dto;
        }).toList();
    }

    // Create and return created DTO
    public ComponentPriceDTO createComponentPrice(ComponentPriceDTO componentPriceDTO) {
        var price = new ComponentPrice();
        modelMapper.map(componentPriceDTO, price);
        var saved = componentPriceRepository.save(price);
        var dto = new ComponentPriceDTO();
        modelMapper.map(saved, dto);
        return dto;
    }

    // Update and return updated DTO
    public ComponentPriceDTO updateComponentPrice(Long id, ComponentPriceDTO componentPriceDTO) {
        var price = componentPriceRepository.findById(id).orElse(null);
        if (price == null) {
            throw new NotFoundException("Price for this component not found");
        }
        modelMapper.map(componentPriceDTO, price);
        var saved = componentPriceRepository.save(price);
        var dto = new ComponentPriceDTO();
        modelMapper.map(saved, dto);
        return dto;
    }

    // Delete by id
    public void deleteComponentPrice(Long id) {
        if (!componentPriceRepository.existsById(id)) {
            throw new NotFoundException("Price for this component not found");
        }
        componentPriceRepository.deleteById(id);
    }
}
