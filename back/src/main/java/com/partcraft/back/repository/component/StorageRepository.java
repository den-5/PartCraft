package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.Storage;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends ComponentsRepository<Storage, Long> {
}
