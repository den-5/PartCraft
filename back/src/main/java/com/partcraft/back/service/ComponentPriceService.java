package com.partcraft.back.service;

import com.partcraft.back.dto.ComponentPriceDTO;
import com.partcraft.back.entity.ComponentPrice;
import com.partcraft.back.repository.ComponentPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ComponentPriceService {

    @Autowired
    private ComponentPriceRepository componentPriceRepository;

    public ComponentPriceDTO getPrice(String componentType, Long componentId, LocalDate time) {
        var price = componentPriceRepository.findByComponentTypeAndComponentIdAndTime(componentType, componentId, time).orElse(null);
        if (price == null) {
            throw new RuntimeException("Price for this component not found");
        }
        return new ComponentPriceDTO(price);
    }

    public List<ComponentPriceDTO> getPriceHistory(String componentType, Long componentId) {
        var prices = componentPriceRepository.findAllByComponentTypeAndComponentId(componentType, componentId).orElse(null);
        if (prices == null || prices.isEmpty()) {
            throw new RuntimeException("Price for this component not found");
        }
        return prices.stream().map(ComponentPriceDTO::new).toList();
    }

    public void addComponentPrice(ComponentPriceDTO componentPriceDTO) {
        var price = new ComponentPrice();
        price.setTime(componentPriceDTO.getTime());
        price.setLocation(componentPriceDTO.getLocation());
        price.setValue(componentPriceDTO.getValue());
        price.setComponentType(componentPriceDTO.getComponentType());
        price.setComponentId(componentPriceDTO.getComponentId());
        componentPriceRepository.save(price);
    }

    public void updateComponentPrice(Long id, ComponentPriceDTO componentPriceDTO) {
        var price = componentPriceRepository.findById(id).orElse(null);
        if (price == null) {
            throw new RuntimeException("Price for this component not found");
        }
        price.setTime(componentPriceDTO.getTime());
        price.setLocation(componentPriceDTO.getLocation());
        price.setValue(componentPriceDTO.getValue());
        price.setComponentType(componentPriceDTO.getComponentType());
        price.setComponentId(componentPriceDTO.getComponentId());
        componentPriceRepository.save(price);
    }

    public void deleteComponentPrice(Long id) {
        if (!componentPriceRepository.existsById(id)) {
            throw new RuntimeException("Price for this component not found");
        }
        componentPriceRepository.deleteById(id);
    }

}
