package com.partcraft.back.service;

import com.partcraft.back.entity.RefreshToken;
import com.partcraft.back.entity.User;
import com.partcraft.back.exception.service.RefreshTokenServiceException;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.security.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtUtils jwtUtils,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    public String createRefreshToken(User user) {
        try {
            String token = jwtUtils.generateRefreshToken(user.getUsername());
            Instant expiryDate = Instant.now().plusSeconds(jwtUtils.getRefreshTokenExpirationInMs());

            var refreshToken = new RefreshToken(token, user, expiryDate);
            refreshTokenRepository.save(refreshToken);
            return refreshToken.getToken();
        } catch (Exception e) {
            throw new RefreshTokenServiceException("Refresh token creation failed: " + e.getMessage());
        }
    }

    public String createRefreshToken(String username) {
        try {
            var user = userRepository.findUserByUsername(username).orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            String token = jwtUtils.generateRefreshToken(user.getUsername());
            Instant expiryDate = Instant.now().plusSeconds(jwtUtils.getRefreshTokenExpirationInMs());

            var refreshToken = new RefreshToken(token, user, expiryDate);
            refreshTokenRepository.save(refreshToken);
            return refreshToken.getToken();

        } catch (Exception e) {
            throw new RefreshTokenServiceException("Refresh token creation failed: " + e.getMessage());
        }
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return jwtUtils.validateRefreshToken(refreshToken) && refreshTokenRepository.findByToken(refreshToken).isPresent();
    }

    public void deleteRefreshToken(String refreshToken) throws RefreshTokenServiceException {
        try {
            if (jwtUtils.validateRefreshToken(refreshToken)) {
                refreshTokenRepository.deleteByToken(refreshToken);
            }
        } catch (Exception e) {
            throw new RefreshTokenServiceException("Failed to delete refresh token: " + e.getMessage());
        }
    }

    public void deleteExpiredTokens() throws RefreshTokenServiceException {
        try {
            refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        } catch (Exception e) {
            throw new RefreshTokenServiceException("Failed to delete expired refresh tokens: " + e.getMessage());
        }
    }


}
