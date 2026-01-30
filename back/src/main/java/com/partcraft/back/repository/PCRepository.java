package com.partcraft.back.repository;

import com.partcraft.back.entity.PC;
import org.springframework.data.jpa.repository.EntityGraph; // <--- 1. ADD IMPORT
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PCRepository extends ComponentsRepository<PC, Long> {

    @EntityGraph(attributePaths = {
            "cpu", "gpu", "ramKit", "storage", "psu",
            "motherboard", "pcCase", "cpuCooler"
    })
    Optional<List<PC>> findAllByOwnerId(Long userId);

    @EntityGraph(attributePaths = {
            "cpu", "gpu", "ramKit", "storage", "psu",
            "motherboard", "pcCase", "cpuCooler"
    })
    Optional<PC> findById(Long id);

    Optional<PC> findPCByOwnerId(Long userId);
}