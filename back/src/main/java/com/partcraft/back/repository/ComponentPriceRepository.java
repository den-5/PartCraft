package com.partcraft.back.repository;

import com.partcraft.back.entity.ComponentPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentPriceRepository extends JpaRepository<ComponentPrice, Long> {
    Optional<ComponentPrice> findByComponentTypeAndComponentIdAndTime(String componentType, Long componentId, LocalDate time);

    Optional<List<ComponentPrice>> findAllByComponentTypeAndComponentId(String componentType, Long componentId);
}

