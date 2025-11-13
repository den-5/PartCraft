package com.partcraft.back.unit;

import com.partcraft.back.entity.RefreshToken;
import com.partcraft.back.entity.User;
import com.partcraft.back.exception.RefreshTokenServiceException;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    private RefreshTokenService refreshTokenService;

    private User testUser;
    private String testToken;
    private long expirationMs;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, jwtUtils, userRepository);

        testUser = new User("testuser", "test@example.com", "password123");
        testUser.setId(1L);
        testToken = "test.refresh.token";
        expirationMs = 86400000L; // 24 hours
    }

    @Nested
    class CreateRefreshTokenWithUserTests {
        @Test
        void createRefreshToken_shouldReturnToken_whenUserIsValid() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String result = refreshTokenService.createRefreshToken(testUser);

            assertNotNull(result);
            assertEquals(testToken, result);
        }

        @Test
        void createRefreshToken_shouldSaveRefreshTokenToRepository() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenService.createRefreshToken(testUser);

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());

            RefreshToken savedToken = captor.getValue();
            assertEquals(testToken, savedToken.getToken());
            assertEquals(testUser, savedToken.getUser());
            assertNotNull(savedToken.getExpiryDate());
        }

        @Test
        void createRefreshToken_shouldSetCorrectExpiryDate() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Instant beforeCreation = Instant.now();
            refreshTokenService.createRefreshToken(testUser);
            Instant afterCreation = Instant.now();

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());

            RefreshToken savedToken = captor.getValue();
            Instant expectedMinExpiry = beforeCreation.plusSeconds(expirationMs);
            Instant expectedMaxExpiry = afterCreation.plusSeconds(expirationMs);

            assertTrue(savedToken.getExpiryDate().isAfter(expectedMinExpiry.minusSeconds(1)));
            assertTrue(savedToken.getExpiryDate().isBefore(expectedMaxExpiry.plusSeconds(1)));
        }

        @Test
        void createRefreshToken_shouldThrowException_whenJwtUtilsThrowsException() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenThrow(new RuntimeException("JWT generation failed"));

            assertThatThrownBy(() -> refreshTokenService.createRefreshToken(testUser))
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Refresh token creation failed");
        }

        @Test
        void createRefreshToken_shouldThrowException_whenRepositorySaveFails() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenThrow(new RuntimeException("Database error"));

            assertThatThrownBy(() -> refreshTokenService.createRefreshToken(testUser))
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Refresh token creation failed");
        }

        @Test
        void createRefreshToken_shouldGenerateTokenWithUsername() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenService.createRefreshToken(testUser);

            verify(jwtUtils).generateRefreshToken(testUser.getUsername());
        }
    }

    @Nested
    class CreateRefreshTokenWithUsernameTests {
        @Test
        void createRefreshToken_shouldReturnToken_whenUsernameIsValid() {
            when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String result = refreshTokenService.createRefreshToken(testUser.getUsername());

            assertNotNull(result);
            assertEquals(testToken, result);
        }

        @Test
        void createRefreshToken_shouldFindUserByUsername() {
            when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenService.createRefreshToken(testUser.getUsername());

            verify(userRepository).findUserByUsername(testUser.getUsername());
        }

        @Test
        void createRefreshToken_shouldThrowException_whenUserNotFound() {
            when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> refreshTokenService.createRefreshToken(testUser.getUsername()))
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Refresh token creation failed");
        }

        @Test
        void createRefreshToken_shouldSaveRefreshTokenWithFoundUser() {
            when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenService.createRefreshToken(testUser.getUsername());

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());

            RefreshToken savedToken = captor.getValue();
            assertEquals(testUser, savedToken.getUser());
            assertEquals(testToken, savedToken.getToken());
        }

        @Test
        void createRefreshToken_shouldThrowException_whenJwtGenerationFails() {
            when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenThrow(new RuntimeException("JWT error"));

            assertThatThrownBy(() -> refreshTokenService.createRefreshToken(testUser.getUsername()))
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Refresh token creation failed");
        }

        @Test
        void createRefreshToken_shouldThrowException_whenRepositorySaveFails() {
            when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> refreshTokenService.createRefreshToken(testUser.getUsername()))
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Refresh token creation failed");
        }
    }

    @Nested
    class IsRefreshTokenValidTests {
        @Test
        void isRefreshTokenValid_shouldReturnTrue_whenTokenIsValidAndExists() {
            RefreshToken refreshToken = new RefreshToken(testToken, testUser, Instant.now().plusSeconds(3600));

            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            when(refreshTokenRepository.findByToken(testToken)).thenReturn(Optional.of(refreshToken));

            boolean result = refreshTokenService.isRefreshTokenValid(testToken);

            assertTrue(result);
        }

        @Test
        void isRefreshTokenValid_shouldReturnFalse_whenTokenIsInvalid() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(false);

            boolean result = refreshTokenService.isRefreshTokenValid(testToken);

            assertFalse(result);
            verify(refreshTokenRepository, never()).findByToken(anyString());
        }

        @Test
        void isRefreshTokenValid_shouldReturnFalse_whenTokenNotFoundInRepository() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            when(refreshTokenRepository.findByToken(testToken)).thenReturn(Optional.empty());

            boolean result = refreshTokenService.isRefreshTokenValid(testToken);

            assertFalse(result);
        }

        @Test
        void isRefreshTokenValid_shouldCallJwtUtilsValidation() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            when(refreshTokenRepository.findByToken(testToken)).thenReturn(Optional.empty());

            refreshTokenService.isRefreshTokenValid(testToken);

            verify(jwtUtils).validateRefreshToken(testToken);
        }

        @Test
        void isRefreshTokenValid_shouldCallRepositoryFindByToken_whenJwtIsValid() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            when(refreshTokenRepository.findByToken(testToken)).thenReturn(Optional.empty());

            refreshTokenService.isRefreshTokenValid(testToken);

            verify(refreshTokenRepository).findByToken(testToken);
        }
    }

    @Nested
    class DeleteRefreshTokenTests {
        @Test
        void deleteRefreshToken_shouldDeleteToken_whenTokenIsValid() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            doNothing().when(refreshTokenRepository).deleteByToken(testToken);

            assertDoesNotThrow(() -> refreshTokenService.deleteRefreshToken(testToken));

            verify(refreshTokenRepository).deleteByToken(testToken);
        }

        @Test
        void deleteRefreshToken_shouldNotDeleteToken_whenTokenIsInvalid() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(false);

            assertDoesNotThrow(() -> refreshTokenService.deleteRefreshToken(testToken));

            verify(refreshTokenRepository, never()).deleteByToken(anyString());
        }

        @Test
        void deleteRefreshToken_shouldValidateTokenBeforeDeleting() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            doNothing().when(refreshTokenRepository).deleteByToken(testToken);

            refreshTokenService.deleteRefreshToken(testToken);

            verify(jwtUtils).validateRefreshToken(testToken);
        }

        @Test
        void deleteRefreshToken_shouldThrowException_whenRepositoryDeleteFails() {
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            doThrow(new RuntimeException("Delete failed")).when(refreshTokenRepository).deleteByToken(testToken);

            assertThatThrownBy(() -> refreshTokenService.deleteRefreshToken(testToken))
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Failed to delete refresh token");
        }

        @Test
        void deleteRefreshToken_shouldThrowException_whenValidationFails() {
            when(jwtUtils.validateRefreshToken(testToken)).thenThrow(new RuntimeException("Validation error"));

            assertThatThrownBy(() -> refreshTokenService.deleteRefreshToken(testToken))
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Failed to delete refresh token");
        }
    }

    @Nested
    class DeleteExpiredTokensTests {
        @Test
        void deleteExpiredTokens_shouldCallRepositoryDeleteByExpiryDateBefore() {
            doNothing().when(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));

            assertDoesNotThrow(() -> refreshTokenService.deleteExpiredTokens());

            verify(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));
        }

        @Test
        void deleteExpiredTokens_shouldPassCurrentTimeToRepository() {
            ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
            doNothing().when(refreshTokenRepository).deleteByExpiryDateBefore(instantCaptor.capture());

            Instant beforeCall = Instant.now();
            refreshTokenService.deleteExpiredTokens();
            Instant afterCall = Instant.now();

            Instant capturedInstant = instantCaptor.getValue();
            assertTrue(capturedInstant.isAfter(beforeCall.minusSeconds(1)));
            assertTrue(capturedInstant.isBefore(afterCall.plusSeconds(1)));
        }

        @Test
        void deleteExpiredTokens_shouldThrowException_whenRepositoryDeleteFails() {
            doThrow(new RuntimeException("Database error")).when(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));

            assertThatThrownBy(() -> refreshTokenService.deleteExpiredTokens())
                    .isInstanceOf(RefreshTokenServiceException.class)
                    .hasMessageContaining("Failed to delete expired refresh tokens");
        }

        @Test
        void deleteExpiredTokens_shouldNotThrowException_whenDeletionSucceeds() {
            doNothing().when(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));

            assertDoesNotThrow(() -> refreshTokenService.deleteExpiredTokens());
        }

        @Test
        void deleteExpiredTokens_shouldCallRepositoryExactlyOnce() {
            doNothing().when(refreshTokenRepository).deleteByExpiryDateBefore(any(Instant.class));

            refreshTokenService.deleteExpiredTokens();

            verify(refreshTokenRepository, times(1)).deleteByExpiryDateBefore(any(Instant.class));
        }
    }

    @Nested
    class IntegrationBehaviorTests {
        @Test
        void service_shouldHandleMultipleTokenCreationsForSameUser() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String token1 = refreshTokenService.createRefreshToken(testUser);
            String token2 = refreshTokenService.createRefreshToken(testUser);

            assertNotNull(token1);
            assertNotNull(token2);
            verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
        }

        @Test
        void service_shouldAllowDeletingRecentlyCreatedToken() {
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(jwtUtils.validateRefreshToken(testToken)).thenReturn(true);
            doNothing().when(refreshTokenRepository).deleteByToken(testToken);

            String token = refreshTokenService.createRefreshToken(testUser);
            assertDoesNotThrow(() -> refreshTokenService.deleteRefreshToken(token));

            verify(refreshTokenRepository).deleteByToken(testToken);
        }

        @Test
        void service_shouldHandleUserLookupAndTokenCreation() {
            when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
            when(jwtUtils.generateRefreshToken(testUser.getUsername())).thenReturn(testToken);
            when(jwtUtils.getRefreshTokenExpirationInMs()).thenReturn(expirationMs);
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String token = refreshTokenService.createRefreshToken(testUser.getUsername());

            assertNotNull(token);
            verify(userRepository).findUserByUsername(testUser.getUsername());
            verify(jwtUtils).generateRefreshToken(testUser.getUsername());
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }
    }
}
