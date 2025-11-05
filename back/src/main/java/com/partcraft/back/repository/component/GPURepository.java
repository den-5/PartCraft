package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.GPU;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GPURepository extends ComponentsRepository<GPU, Long> {
}
