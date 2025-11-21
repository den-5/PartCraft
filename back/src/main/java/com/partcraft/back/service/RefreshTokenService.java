package com.partcraft.back.service;

import com.partcraft.back.entity.RefreshToken;
import com.partcraft.back.entity.User;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.security.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtUtils jwtUtils,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(Long userId, long expirationMs) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(expirationMs);

        RefreshToken refreshToken = new RefreshToken(token, userId, expiryDate);
        return refreshTokenRepository.save(refreshToken);
    }

    // Backward-compatible method for creating refresh token with User entity
    public String createRefreshToken(User user) {
        String token = jwtUtils.generateRefreshToken(user.getUsername());
        Instant expiryDate = Instant.now().plusMillis(jwtUtils.getRefreshTokenExpirationInMs());

        RefreshToken refreshToken = new RefreshToken(token, user.getId(), expiryDate);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    // Backward-compatible method for creating refresh token with username
    public String createRefreshToken(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return createRefreshToken(user);
    }

    // Backward-compatible method for validating refresh token
    public boolean isRefreshTokenValid(String refreshToken) {
        return jwtUtils.validateRefreshToken(refreshToken) &&
                refreshTokenRepository.findByToken(refreshToken).isPresent();
    }

    // Backward-compatible method for deleting refresh token
    public void deleteRefreshToken(String refreshToken) {
        if (jwtUtils.validateRefreshToken(refreshToken)) {
            refreshTokenRepository.deleteById(refreshToken);
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public void deleteToken(String token) {
        refreshTokenRepository.deleteById(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }
}
