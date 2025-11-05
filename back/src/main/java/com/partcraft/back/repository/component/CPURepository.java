package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.CPU;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CPURepository extends ComponentsRepository<CPU, Long> {
}
