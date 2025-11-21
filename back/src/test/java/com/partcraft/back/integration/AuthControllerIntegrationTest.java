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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthControllerIntegrationTest extends BaseIntegrationTest {
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
        void shouldCreateUserAndReturn200WithValidData() throws Exception {
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
        @DisplayName("Should create tokens for user in Redis")
        void shouldCreateTokenForNewUserInRedis() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            var tokens = refreshTokenRepository.findAll();
            long count = 0;
            for (var token : tokens) {
                count++;
            }
            assert count == 1;
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
        @DisplayName("login should create RefreshToken entity in Redis and return status 200")
        void shouldReturnStatus200AndCreateRefreshTokenEntity() throws Exception {
            var request = sampleLoginRequestDTO();

            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            var tokens = refreshTokenRepository.findAll();
            long count = 0;
            for (var token : tokens) {
                count++;
            }
            assert count == 1;
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
            long count = 0;
            for (var token : tokens) {
                count++;
            }
            assert count == 0;
        }
    }

    private CreateUserDTO sampleCreateUserDTO() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("Password123!");
        return dto;
    }

    private LoginRequestDTO sampleLoginRequestDTO() {
        return new LoginRequestDTO("test@example.com", "Password123!");
    }
}

