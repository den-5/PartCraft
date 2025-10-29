package com.partcraft.back.repository;

import com.partcraft.back.entity.RefreshToken;
import com.partcraft.back.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Transactional
    @Modifying
    void deleteByToken(String token);

    @Transactional
    @Modifying
    void deleteByExpiryDateBefore(Instant date);

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    @Modifying
    void deleteAllByUser(User user);
}
