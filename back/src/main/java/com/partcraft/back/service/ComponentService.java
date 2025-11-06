package com.partcraft.back.service;

import com.partcraft.back.exception.ComponentServiceException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
public abstract class ComponentService<T, DTO, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract T toEntity(DTO dto);

    protected abstract DTO toDTO(T entity);

    public DTO create(DTO dto) {
        T entity = toEntity(dto);
        T saved = getRepository().save(entity);
        return toDTO(saved);
    }

    public DTO update(ID id, DTO dto) {
        if (!getRepository().existsById(id)) {
            throw new ComponentServiceException("Entity with id " + id + " not found");
        }
        T entity = toEntity(dto);
        T updated = getRepository().save(entity);
        return toDTO(updated);
    }

    public DTO findById(ID id) {
        return getRepository().findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ComponentServiceException("Entity with id " + id + " not found"));
    }

    public List<DTO> findAll() {
        return getRepository().findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(ID id) {
        if (!getRepository().existsById(id)) {
            throw new ComponentServiceException("Entity with id " + id + " not found");
        }
        getRepository().deleteById(id);
    }
}
