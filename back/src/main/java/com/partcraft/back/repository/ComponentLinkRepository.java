package com.partcraft.back.repository;

import com.partcraft.back.entity.ComponentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentLinkRepository extends JpaRepository<ComponentLink, Long> {
    Optional<List<ComponentLink>> findAllByComponentIdAndComponentType(Long componentId, String componentType);
}
