package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.PSU;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PSURepository extends ComponentsRepository<PSU, Long> {
}
