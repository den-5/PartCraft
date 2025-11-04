package com.partcraft.back.repository;

import com.partcraft.back.entity.PC;

import java.util.List;
import java.util.Optional;

public interface PCRepository extends ComponentsRepository<PC, Long> {
    Optional<PC> findPCByOwnerId(Long userId);

    Optional<List<PC>> findAllByOwnerId(Long userId);
}
