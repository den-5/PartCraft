package com.partcraft.back.unit;

import com.partcraft.back.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String testSecret = "b93e6c1c588d97ca2f7dbc938c7ccd461668f1ad0a390a9f2b77bf3aa8fa8bb4a319c74c5d250f02288b7ebac581edf077d93e677bce8faf368c2e8ffbb9e185";
    private final long jwtExpirationMs = 1800000; // 30 minutes
    private final long refreshTokenExpirationInMs = 86400000; // 24 hours
    private Key secretKey;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationInMs", jwtExpirationMs);
        ReflectionTestUtils.setField(jwtUtils, "jwtRefreshTokenExpirationInMs", refreshTokenExpirationInMs);

        // Initialize the secret key
        jwtUtils.init();

        // Create secret key for manual token generation
        byte[] keyBytes = Base64.getDecoder().decode(testSecret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Nested
    class InitTests {
        @Test
        void init_shouldInitializeSecretKey() {
            JwtUtils newJwtUtils = new JwtUtils();
            ReflectionTestUtils.setField(newJwtUtils, "jwtSecret", testSecret);

            newJwtUtils.init();

            // Verify that secretKey is initialized (not null)
            Object secretKeyField = ReflectionTestUtils.getField(newJwtUtils, "secretKey");
            assertNotNull(secretKeyField);
        }
    }

    @Nested
    class GenerateAccessTokenTests {
        @Test
        void generateToken_shouldReturnValidJwtToken() {
            String username = "testuser";

            String token = jwtUtils.generateToken(username);

            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
        }

        @Test
        void generateToken_shouldContainCorrectUsername() {
            String username = "john228";

            String token = jwtUtils.generateToken(username);
            String extractedUsername = jwtUtils.getUsernameFromToken(token);

            assertEquals(username, extractedUsername);
        }

        @Test
        void generateToken_shouldHaveCorrectExpiration() {
            String username = "testuser";
            long beforeGeneration = System.currentTimeMillis();

            String token = jwtUtils.generateToken(username);

            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            long expectedExpiration = beforeGeneration + jwtExpirationMs;
            long actualExpiration = expiration.getTime();

            // Allow 1 second tolerance
            assertTrue(Math.abs(actualExpiration - expectedExpiration) < 1000);
        }

        @Test
        void generateToken_shouldGenerateDifferentTokens_forSameUserAtDifferentTimes() throws InterruptedException {
            String username = "testuser";

            String token1 = jwtUtils.generateToken(username);
            Thread.sleep(1000); // Wait 1 second to ensure different timestamps
            String token2 = jwtUtils.generateToken(username);

            assertNotEquals(token1, token2);
        }

        @Test
        void generateToken_shouldGenerateDifferentTokens_forDifferentUsers() {
            String token1 = jwtUtils.generateToken("user1");
            String token2 = jwtUtils.generateToken("user2");

            assertNotEquals(token1, token2);
        }
    }

    @Nested
    class GenerateRefreshTokenTests {
        @Test
        void generateRefreshToken_shouldReturnValidJwtToken() {
            String username = "testuser";

            String refreshToken = jwtUtils.generateRefreshToken(username);

            assertNotNull(refreshToken);
            assertFalse(refreshToken.isEmpty());
            assertTrue(refreshToken.split("\\.").length == 3);
        }

        @Test
        void generateRefreshToken_shouldContainCorrectUsername() {
            String username = "jane123";

            String refreshToken = jwtUtils.generateRefreshToken(username);
            String extractedUsername = jwtUtils.getUsernameFromRefreshToken(refreshToken);

            assertEquals(username, extractedUsername);
        }

        @Test
        void generateRefreshToken_shouldHaveCorrectExpiration() {
            String username = "testuser";
            long beforeGeneration = System.currentTimeMillis();

            String refreshToken = jwtUtils.generateRefreshToken(username);

            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getExpiration();

            long expectedExpiration = beforeGeneration + refreshTokenExpirationInMs;
            long actualExpiration = expiration.getTime();

            // Allow 1 second tolerance
            assertTrue(Math.abs(actualExpiration - expectedExpiration) < 1000);
        }

        @Test
        void generateRefreshToken_shouldGenerateDifferentTokens_forSameUserAtDifferentTimes() throws InterruptedException {
            String username = "testuser";

            String refreshToken1 = jwtUtils.generateRefreshToken(username);
            Thread.sleep(1000); // Wait 1 second to ensure different timestamps
            String refreshToken2 = jwtUtils.generateRefreshToken(username);

            assertNotEquals(refreshToken1, refreshToken2);
        }

        @Test
        void getRefreshTokenExpirationInMs_shouldReturnCorrectValue() {
            long expirationMs = jwtUtils.getRefreshTokenExpirationInMs();

            assertEquals(refreshTokenExpirationInMs, expirationMs);
        }
    }

    @Nested
    class ExtractUsernameTests {
        @Test
        void getUsernameFromToken_shouldExtractCorrectUsername() {
            String username = "alice456";
            String token = jwtUtils.generateToken(username);

            String extractedUsername = jwtUtils.getUsernameFromToken(token);

            assertEquals(username, extractedUsername);
        }

        @Test
        void getUsernameFromRefreshToken_shouldExtractCorrectUsername() {
            String username = "bob789";
            String refreshToken = jwtUtils.generateRefreshToken(username);

            String extractedUsername = jwtUtils.getUsernameFromRefreshToken(refreshToken);

            assertEquals(username, extractedUsername);
        }
    }

    @Nested
    class ValidateAccessTokenTests {
        @Test
        void validateToken_shouldReturnTrue_whenTokenIsValid() {
            String username = "validuser";
            String token = jwtUtils.generateToken(username);

            boolean isValid = jwtUtils.validateToken(token);

            assertTrue(isValid);
        }

        @Test
        void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
            String invalidToken = "invalid.jwt.token";

            boolean isValid = jwtUtils.validateToken(invalidToken);

            assertFalse(isValid);
        }

        @Test
        void validateToken_shouldReturnFalse_whenTokenIsExpired() {
            String username = "expireduser";

            // Create an expired token (expired 1 hour ago)
            String expiredToken = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
                    .setExpiration(new Date(System.currentTimeMillis() - 1800000))
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

            boolean isValid = jwtUtils.validateToken(expiredToken);

            assertFalse(isValid);
        }

        @Test
        void validateToken_shouldReturnFalse_whenTokenHasInvalidSignature() {
            String username = "testuser";

            // Create a token with a different secret key
            Key wrongSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            String tokenWithWrongSignature = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(wrongSecretKey, SignatureAlgorithm.HS512)
                    .compact();

            boolean isValid = jwtUtils.validateToken(tokenWithWrongSignature);

            assertFalse(isValid);
        }

        @Test
        void validateToken_shouldReturnFalse_whenTokenIsNull() {
            boolean isValid = jwtUtils.validateToken(null);

            assertFalse(isValid);
        }

        @Test
        void validateToken_shouldReturnFalse_whenTokenIsEmpty() {
            boolean isValid = jwtUtils.validateToken("");

            assertFalse(isValid);
        }
    }

    @Nested
    class ValidateRefreshTokenTests {
        @Test
        void validateRefreshToken_shouldReturnTrue_whenRefreshTokenIsValid() {
            String username = "validuser";
            String refreshToken = jwtUtils.generateRefreshToken(username);

            boolean isValid = jwtUtils.validateRefreshToken(refreshToken);

            assertTrue(isValid);
        }

        @Test
        void validateRefreshToken_shouldReturnFalse_whenRefreshTokenIsInvalid() {
            String invalidRefreshToken = "invalid.refresh.token";

            boolean isValid = jwtUtils.validateRefreshToken(invalidRefreshToken);

            assertFalse(isValid);
        }

        @Test
        void validateRefreshToken_shouldReturnFalse_whenRefreshTokenIsExpired() {
            String username = "expireduser";

            // Create an expired refresh token
            String expiredRefreshToken = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis() - refreshTokenExpirationInMs - 3600000))
                    .setExpiration(new Date(System.currentTimeMillis() - 3600000))
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

            boolean isValid = jwtUtils.validateRefreshToken(expiredRefreshToken);

            assertFalse(isValid);
        }
    }

    @Nested
    class TokenComparisonTests {
        @Test
        void accessTokenAndRefreshToken_shouldBeDifferent_forSameUser() {
            String username = "testuser";

            String accessToken = jwtUtils.generateToken(username);
            String refreshToken = jwtUtils.generateRefreshToken(username);

            assertNotEquals(accessToken, refreshToken);
        }
    }
}
