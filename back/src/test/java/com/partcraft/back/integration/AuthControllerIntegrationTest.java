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
import org.springframework.mock.web.MockCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    void clean() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Successful sign-Up tests")
    class SuccessfulSignUpTests {
        @Test
        @DisplayName("Should create user and return 200 with valid data and set cookies")
        void shouldCreateUserAndReturn200WithValidData() throws Exception {
            CreateUserDTO request = sampleCreateUserDTO();
            var result = mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(request.getUsername()))
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.email").value(request.getEmail()))
                    .andExpect(cookie().exists("accessToken"))
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().httpOnly("accessToken", true))
                    .andExpect(cookie().httpOnly("refreshToken", true))
                    .andReturn();

            // Verify cookies have values
            assertNotNull(result.getResponse().getCookie("accessToken"));
            assertNotNull(result.getResponse().getCookie("refreshToken"));
            assertFalse(result.getResponse().getCookie("accessToken").getValue().isEmpty());
            assertFalse(result.getResponse().getCookie("refreshToken").getValue().isEmpty());
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
        @DisplayName("login should return UserDTO and status 200 with cookies")
        void shouldReturnStatus200AndUserDTO() throws Exception {
            var request = sampleLoginRequestDTO();

            User user = new User("testuser", "test@example.com", passwordEncoder.encode("Password123!"));
            userRepository.save(user);

            var result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(user.getUsername()))
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(cookie().exists("accessToken"))
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().httpOnly("accessToken", true))
                    .andExpect(cookie().httpOnly("refreshToken", true))
                    .andReturn();

            // Verify cookies have values
            assertNotNull(result.getResponse().getCookie("accessToken"));
            assertNotNull(result.getResponse().getCookie("refreshToken"));
            assertFalse(result.getResponse().getCookie("accessToken").getValue().isEmpty());
            assertFalse(result.getResponse().getCookie("refreshToken").getValue().isEmpty());
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

    @Nested
    @DisplayName("Logout endpoint tests")
    class LogoutEndpointTests {
        @Test
        @DisplayName("Should logout and delete refresh token when valid cookie is provided")
        void shouldLogoutAndDeleteRefreshToken() throws Exception {
            // Register and login user to get refresh token
            CreateUserDTO request = sampleCreateUserDTO();
            var signUpResult = mockMvc.perform(post("/api/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            String refreshToken = signUpResult.getResponse().getCookie("refreshToken").getValue();
            assertNotNull(refreshToken);
            assertFalse(refreshToken.isEmpty());
            assertTrue(refreshTokenRepository.findAll().iterator().hasNext());

            // Call logout with refresh token cookie using MockCookie
            mockMvc.perform(post("/api/auth/logout")
                            .cookie(new MockCookie("refreshToken", refreshToken)))
                    .andExpect(status().isOk());

            // Token should be deleted
            assertFalse(refreshTokenRepository.findAll().iterator().hasNext());
        }

        @Test
        @DisplayName("Should return 400 if no refresh token is provided")
        void shouldReturn400IfNoRefreshTokenProvided() throws Exception {
            mockMvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 200 if refresh token is invalid or already deleted")
        void shouldReturn200IfRefreshTokenInvalidOrDeleted() throws Exception {
            // Use a random/invalid token with MockCookie
            String invalidToken = "invalidtoken123";
            mockMvc.perform(post("/api/auth/logout")
                            .cookie(new MockCookie("refreshToken", invalidToken)))
                    .andExpect(status().isOk());
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
