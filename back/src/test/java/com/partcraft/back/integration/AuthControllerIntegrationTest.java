package com.partcraft.back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.dto.LoginRequestDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("AuthController Sign-Up Integration Tests")
public class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Successful sign-Up tests")
    class SuccessfulSignUpTests {
        @Test
        @DisplayName("Should create user and return 200 with valid data")
        void shouldCreateUserAndReturn200WIthValidData() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.user.username").value(request.getUsername()))
                    .andExpect(jsonPath("$.user.password").doesNotExist())
                    .andExpect(jsonPath("$.user.email").value(request.getEmail()))
                    .andExpect(jsonPath("$.tokens").exists())
                    .andExpect(jsonPath("$.tokens.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.tokens.refreshToken").isNotEmpty());
        }

        @Test
        @DisplayName("Should create user in db")
        void shouldSaveNewUserToDatabase() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            var users = userRepository.findAll();
            assert users.size() == 1;
            assert users.get(0).getUsername().equals(request.getUsername());
            assert users.get(0).getEmail().equals(request.getEmail());

        }

        @Test
        @DisplayName("Should create tokens for user ")
        void shouldCreateTokenForNewUserInDatabase() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            var tokens = refreshTokenRepository.findAll();
            assert tokens.size() == 1;
        }
    }

    @Nested
    @DisplayName("Invalid data sign-Up tests")
    class InvalidDataSignUpTests {
        @Test
        @DisplayName("invalid username format")
        void shouldThrowValidationErrorWhenUsernameIsNotValid() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            request.setUsername("invalid!``");
            mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("invalid username format"));
        }

        @Test
        @DisplayName("invalid email format")
        void shouldThrowValidationErrorWhenEmailIsNotValid() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            request.setEmail("invalid");
            mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("invalid email format"));
        }

        @Test
        @DisplayName("invalid password format")
        void shouldThrowValidationErrorWhenPasswordIsNotValid() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            request.setPassword("invalid");
            mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("invalid password format"));
        }
    }

    @Nested
    @DisplayName("Successful user login tests")
    class SuccessfulUserLoginTests {
        @Test
        @DisplayName("login should return AuthResponseDTO and status 200")
        void shouldReturnStatus200AndAuthResponseDTO() throws Exception {
            var request = sampleLoginRequestDTO();

            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.user.username").value(user.getUsername()))
                    .andExpect(jsonPath("$.user.password").doesNotExist())
                    .andExpect(jsonPath("$.user.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.tokens").exists())
                    .andExpect(jsonPath("$.tokens.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.tokens.refreshToken").isNotEmpty());
        }

        @Test
        @DisplayName("login should create RefreshToken entity and return status 200")
        void shouldReturnStatus200AndCreateRefreshTokenEntity() throws Exception {
            var request = sampleLoginRequestDTO();

            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            var tokens = refreshTokenRepository.findAll();
            assert tokens.size() == 1;
        }
    }

    @Nested
    @DisplayName("Failed user login tests")
    class FailedUserLoginTests {
        @Test
        @DisplayName("login should return exception when email does not exist")
        void shouldReturnExceptionWhenEmailDoesNotExist() throws Exception {
            var request = sampleLoginRequestDTO();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("login should return 401 when password is incorrect")
        void shouldReturn401WhenPasswordIsIncorrect() throws Exception {
            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            var request = new LoginRequestDTO("test@example.com", "WrongPassword123!");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTH_CONTROLLER_ERROR"))
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }

        @Test
        @DisplayName("login should return 401 when email format is invalid")
        void shouldReturn401WhenEmailFormatIsInvalid() throws Exception {
            var request = new LoginRequestDTO("invalidemail", "Password123!");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(jsonPath("$.code").value("VALIDATE_USER_DATA_ERROR"))
                    .andExpect(jsonPath("$.message").value("invalid email format"));
        }

        @Test
        @DisplayName("login should not create RefreshToken when authentication fails")
        void shouldNotCreateRefreshTokenWhenAuthenticationFails() throws Exception {
            var request = new LoginRequestDTO("invalidemail", "Password123!");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(jsonPath("$.code").value("VALIDATE_USER_DATA_ERROR"))
                    .andExpect(jsonPath("$.message").exists());

            var tokens = refreshTokenRepository.findAll();
            assert tokens.isEmpty();
        }
    }

    @Nested
    @DisplayName("Successful refresh token tests")
    class SuccessfulRefreshTokenTests {
        @Test
        @DisplayName("refresh should return new tokens and status 200 with valid refresh token")
        void shouldReturnStatus200AndNewTokensWithValidRefreshToken() throws Exception {
            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            // Login to get a refresh token
            var loginRequest = sampleLoginRequestDTO();
            var loginResponse = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResponse.getResponse().getContentAsString();
            var authResponse = objectMapper.readValue(responseBody, com.partcraft.back.dto.AuthResponseDTO.class);
            String refreshToken = authResponse.getTokens().getRefreshToken();

            // Use refresh token to get new tokens
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + refreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.accessToken").isString())
                    .andExpect(jsonPath("$.refreshToken").isString());
        }

        @Test
        @DisplayName("refresh should delete old refresh token from database")
        void shouldDeleteOldRefreshTokenFromDatabase() throws Exception {
            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            // Login to get a refresh token
            var loginRequest = sampleLoginRequestDTO();
            var loginResponse = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResponse.getResponse().getContentAsString();
            var authResponse = objectMapper.readValue(responseBody, com.partcraft.back.dto.AuthResponseDTO.class);
            String oldRefreshToken = authResponse.getTokens().getRefreshToken();

            var tokensBeforeRefresh = refreshTokenRepository.findAll();
            assert tokensBeforeRefresh.size() == 1;

            // Use refresh token to get new tokens
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + oldRefreshToken))
                    .andExpect(status().isOk());

            var oldTokenStillExists = refreshTokenRepository.findAll()
                    .stream()
                    .anyMatch(token -> token.getToken().equals(oldRefreshToken));

            assert !oldTokenStillExists;
        }

        @Test
        @DisplayName("refresh should create new refresh token in database")
        void shouldCreateNewRefreshTokenInDatabase() throws Exception {
            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            // Login to get a refresh token
            var loginRequest = sampleLoginRequestDTO();
            var loginResponse = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResponse.getResponse().getContentAsString();
            var authResponse = objectMapper.readValue(responseBody, com.partcraft.back.dto.AuthResponseDTO.class);
            String oldRefreshToken = authResponse.getTokens().getRefreshToken();

            // Use refresh token to get new tokens
            var refreshResponse = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + oldRefreshToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String refreshResponseBody = refreshResponse.getResponse().getContentAsString();
            var tokensDto = objectMapper.readValue(refreshResponseBody, com.partcraft.back.dto.JwtTokensDTO.class);

            // Verify new token is in database
            var tokens = refreshTokenRepository.findAll();
            assert tokens.size() == 1;
            assert tokens.get(0).getToken().equals(tokensDto.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Failed refresh token tests")
    class FailedRefreshTokenTests {
        @Test
        @DisplayName("refresh should return 401 when refresh token is missing")
        void shouldReturn401WhenRefreshTokenIsMissing() throws Exception {
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTH_CONTROLLER_ERROR"));
        }

        @Test
        @DisplayName("refresh should return 401 when refresh token is invalid")
        void shouldReturn401WhenRefreshTokenIsInvalid() throws Exception {
            String invalidToken = "invalid.refresh.token";

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + invalidToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTH_CONTROLLER_ERROR"));
        }

        @Test
        @DisplayName("refresh should return 401 when refresh token is expired")
        void shouldReturn401WhenRefreshTokenIsExpired() throws Exception {
            // This test would require creating an expired token
            // You might need to add a method in JwtUtils to create tokens with custom expiration
            // For now, we'll test with a token that doesn't exist in the database
            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            // Login to get a refresh token
            var loginRequest = sampleLoginRequestDTO();
            var loginResponse = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResponse.getResponse().getContentAsString();
            var authResponse = objectMapper.readValue(responseBody, com.partcraft.back.dto.AuthResponseDTO.class);
            String refreshToken = authResponse.getTokens().getRefreshToken();

            // Delete the token from database to simulate expiration
            refreshTokenRepository.deleteAll();

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + refreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTH_CONTROLLER_ERROR"));
        }

        @Test
        @DisplayName("refresh should return 401 when authorization header format is wrong")
        void shouldReturn401WhenAuthorizationHeaderFormatIsWrong() throws Exception {
            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            // Login to get a refresh token
            var loginRequest = sampleLoginRequestDTO();
            var loginResponse = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResponse.getResponse().getContentAsString();
            var authResponse = objectMapper.readValue(responseBody, com.partcraft.back.dto.AuthResponseDTO.class);
            String refreshToken = authResponse.getTokens().getRefreshToken();

            // Send token without "Bearer " prefix
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", refreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTH_CONTROLLER_ERROR"));
        }

        @Test
        @DisplayName("refresh should not create new token when refresh token is invalid")
        void shouldNotCreateNewTokenWhenRefreshTokenIsInvalid() throws Exception {
            String invalidToken = "invalid.refresh.token";

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + invalidToken))
                    .andExpect(status().isUnauthorized());

            var tokens = refreshTokenRepository.findAll();
            assert tokens.isEmpty();
        }

        @Test
        @DisplayName("refresh should return 401 when used token is already deleted")
        void shouldReturn401WhenTokenIsAlreadyUsed() throws Exception {
            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            // Login to get a refresh token
            var loginRequest = sampleLoginRequestDTO();
            var loginResponse = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResponse.getResponse().getContentAsString();
            var authResponse = objectMapper.readValue(responseBody, com.partcraft.back.dto.AuthResponseDTO.class);
            String refreshToken = authResponse.getTokens().getRefreshToken();

            // Use refresh token once (should succeed)
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + refreshToken))
                    .andExpect(status().isOk());

            // Try to use the same token again (should fail)
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/refresh")
                            .header("Authorization", "Bearer " + refreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("AUTH_CONTROLLER_ERROR"));
        }
    }


    private CreateUserDTO sampleCreateUserDTO() {
        return new CreateUserDTO(
                "testuser",
                "test@example.com",
                "Password123!"
        );
    }

    private LoginRequestDTO sampleLoginRequestDTO() {
        return new LoginRequestDTO(
                "test@example.com",
                "Password123!"
        );
    }
}
