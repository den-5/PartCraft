package com.partcraft.back.unit;

import com.partcraft.back.entity.RefreshToken;
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

    private Long testUserId;
    private String testToken;
    private long expirationMs;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, jwtUtils, userRepository);

        testUserId = 1L;
        testToken = "test-refresh-token-uuid";
        expirationMs = 86400000L; // 24 hours
    }

    @Nested
    class CreateRefreshTokenTests {
        @Test
        void createRefreshToken_shouldReturnRefreshToken_whenValidInput() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            RefreshToken result = refreshTokenService.createRefreshToken(testUserId, expirationMs);

            assertNotNull(result);
            assertNotNull(result.getToken());
            assertEquals(testUserId, result.getUserId());
        }

        @Test
        void createRefreshToken_shouldSaveRefreshTokenToRepository() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenService.createRefreshToken(testUserId, expirationMs);

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());

            RefreshToken savedToken = captor.getValue();
            assertNotNull(savedToken.getToken());
            assertEquals(testUserId, savedToken.getUserId());
            assertNotNull(savedToken.getExpiryDate());
            assertNotNull(savedToken.getTtl());
        }

        @Test
        void createRefreshToken_shouldSetCorrectExpiryDate() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Instant beforeCreation = Instant.now();
            RefreshToken result = refreshTokenService.createRefreshToken(testUserId, expirationMs);
            Instant afterCreation = Instant.now();

            Instant expectedMinExpiry = beforeCreation.plusMillis(expirationMs);
            Instant expectedMaxExpiry = afterCreation.plusMillis(expirationMs);

            assertTrue(result.getExpiryDate().isAfter(expectedMinExpiry.minusSeconds(1)));
            assertTrue(result.getExpiryDate().isBefore(expectedMaxExpiry.plusSeconds(1)));
        }

        @Test
        void createRefreshToken_shouldGenerateUniqueToken() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            RefreshToken token1 = refreshTokenService.createRefreshToken(testUserId, expirationMs);
            RefreshToken token2 = refreshTokenService.createRefreshToken(testUserId, expirationMs);

            assertNotNull(token1.getToken());
            assertNotNull(token2.getToken());
            assertNotEquals(token1.getToken(), token2.getToken());
        }

        @Test
        void createRefreshToken_shouldCalculateTTL() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

            RefreshToken result = refreshTokenService.createRefreshToken(testUserId, expirationMs);

            assertNotNull(result.getTtl());
            assertTrue(result.getTtl() > 0);
            // TTL should be roughly equal to expirationMs in seconds (within a small margin)
            long expectedTtl = expirationMs / 1000;
            assertTrue(result.getTtl() >= expectedTtl - 2 && result.getTtl() <= expectedTtl + 2);
        }
    }

    @Nested
    class FindByTokenTests {
        @Test
        void findByToken_shouldReturnToken_whenTokenExists() {
            RefreshToken refreshToken = new RefreshToken(testToken, testUserId, Instant.now().plusSeconds(3600));
            when(refreshTokenRepository.findByToken(testToken)).thenReturn(Optional.of(refreshToken));

            Optional<RefreshToken> result = refreshTokenService.findByToken(testToken);

            assertTrue(result.isPresent());
            assertEquals(testToken, result.get().getToken());
            assertEquals(testUserId, result.get().getUserId());
        }

        @Test
        void findByToken_shouldReturnEmpty_whenTokenDoesNotExist() {
            when(refreshTokenRepository.findByToken(testToken)).thenReturn(Optional.empty());

            Optional<RefreshToken> result = refreshTokenService.findByToken(testToken);

            assertFalse(result.isPresent());
        }

        @Test
        void findByToken_shouldCallRepository() {
            when(refreshTokenRepository.findByToken(testToken)).thenReturn(Optional.empty());

            refreshTokenService.findByToken(testToken);

            verify(refreshTokenRepository).findByToken(testToken);
        }
    }

    @Nested
    class VerifyExpirationTests {
        @Test
        void verifyExpiration_shouldReturnToken_whenTokenIsNotExpired() {
            RefreshToken refreshToken = new RefreshToken(testToken, testUserId, Instant.now().plusSeconds(3600));

            RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

            assertNotNull(result);
            assertEquals(refreshToken, result);
            verify(refreshTokenRepository, never()).delete(any());
        }

        @Test
        void verifyExpiration_shouldThrowException_whenTokenIsExpired() {
            RefreshToken refreshToken = new RefreshToken(testToken, testUserId, Instant.now().minusSeconds(3600));
            doNothing().when(refreshTokenRepository).delete(refreshToken);

            assertThatThrownBy(() -> refreshTokenService.verifyExpiration(refreshToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Refresh token expired");
        }

        @Test
        void verifyExpiration_shouldDeleteToken_whenTokenIsExpired() {
            RefreshToken refreshToken = new RefreshToken(testToken, testUserId, Instant.now().minusSeconds(3600));
            doNothing().when(refreshTokenRepository).delete(refreshToken);

            try {
                refreshTokenService.verifyExpiration(refreshToken);
            } catch (RuntimeException e) {
                // Expected
            }

            verify(refreshTokenRepository).delete(refreshToken);
        }

        @Test
        void verifyExpiration_shouldNotDeleteToken_whenTokenIsValid() {
            RefreshToken refreshToken = new RefreshToken(testToken, testUserId, Instant.now().plusSeconds(3600));

            refreshTokenService.verifyExpiration(refreshToken);

            verify(refreshTokenRepository, never()).delete(any());
        }
    }

    @Nested
    class DeleteTokenTests {
        @Test
        void deleteToken_shouldDeleteTokenById() {
            doNothing().when(refreshTokenRepository).deleteById(testToken);

            assertDoesNotThrow(() -> refreshTokenService.deleteToken(testToken));

            verify(refreshTokenRepository).deleteById(testToken);
        }

        @Test
        void deleteToken_shouldCallRepositoryDeleteById() {
            doNothing().when(refreshTokenRepository).deleteById(testToken);

            refreshTokenService.deleteToken(testToken);

            verify(refreshTokenRepository, times(1)).deleteById(testToken);
        }
    }

    @Nested
    class DeleteByUserIdTests {
        @Test
        void deleteByUserId_shouldCallRepositoryDeleteByUserId() {
            doNothing().when(refreshTokenRepository).deleteByUserId(testUserId);

            assertDoesNotThrow(() -> refreshTokenService.deleteByUserId(testUserId));

            verify(refreshTokenRepository).deleteByUserId(testUserId);
        }

        @Test
        void deleteByUserId_shouldDeleteAllTokensForUser() {
            doNothing().when(refreshTokenRepository).deleteByUserId(testUserId);

            refreshTokenService.deleteByUserId(testUserId);

            verify(refreshTokenRepository, times(1)).deleteByUserId(testUserId);
        }
    }
}

