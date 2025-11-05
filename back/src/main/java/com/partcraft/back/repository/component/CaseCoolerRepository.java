package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.CaseCooler;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseCoolerRepository extends ComponentsRepository<CaseCooler, Long> {
}
