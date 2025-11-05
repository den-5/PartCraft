package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.ComponentPlacement;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentPlacementRepository extends ComponentsRepository<ComponentPlacement, Long> {
}
