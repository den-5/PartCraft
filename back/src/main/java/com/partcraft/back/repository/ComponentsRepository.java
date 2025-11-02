package com.partcraft.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentsRepository<T, ID> extends JpaRepository<T, ID> {
}

