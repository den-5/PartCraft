package com.partcraft.back.service.component;

import com.partcraft.back.dto.componentDTO.StorageDTO;
import com.partcraft.back.entity.component.Storage;
import com.partcraft.back.repository.component.StorageRepository;
import com.partcraft.back.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StorageService extends ComponentService<Storage, StorageDTO, Long> {

    @Autowired
    private StorageRepository storageRepository;

    @Override
    protected JpaRepository<Storage, Long> getRepository() {
        return storageRepository;
    }

    @Override
    protected Storage toEntity(StorageDTO dto) {
        return new Storage(dto);
    }

    @Override
    protected StorageDTO toDTO(Storage entity) {
        return new StorageDTO(entity);
    }
}

