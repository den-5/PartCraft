package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.CPUCooler;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CPUCoolerRepository extends ComponentsRepository<CPUCooler, Long> {
}
