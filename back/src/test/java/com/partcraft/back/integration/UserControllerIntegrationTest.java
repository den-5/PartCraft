package com.partcraft.back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.User.UpdateUserDTO;
import com.partcraft.back.integration.helper.TestUtils;
import com.partcraft.back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.mock.web.MockCookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController Integration Tests")
public class UserControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    private MockCookie adminCookie;
    private MockCookie userCookie;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        adminCookie = testUtils.createAdminAndGetAccessCookie();
        userCookie = testUtils.createUserAndGetAccessCookie();
    }

    @Nested
    @DisplayName("Get user tests")
    class GetUserTests {
        @Test
        @DisplayName("Should return user data")
        void shouldFindUserAndReturn200withValidData() throws Exception {
            String response = mockMvc.perform(get("/api/user/")
                            .cookie(userCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // Parse and assert user data
            var json = objectMapper.readTree(response);
            assertEquals("testuser", json.get("username").asText());
            assertEquals("test@example.com", json.get("email").asText());
        }

        @Test
        @DisplayName("Should return 401 Unauthorized if access token is invalid")
        void shouldReturn401UnauthorizedIfAccessTokenIsInvalid() throws Exception {
            MockCookie invalidCookie = new MockCookie("accessToken", "invalidToken12345");
            mockMvc.perform(get("/api/user/")
                            .cookie(invalidCookie))
                    .andExpect(status().isUnauthorized());
            mockMvc.perform(get("/api/user/testuser")
                            .cookie(invalidCookie))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return user data by username")
        void shouldFindUserByUsernameAndReturn200withValidData() throws Exception {
            String response = mockMvc.perform(get("/api/user/testuser")
                            .cookie(userCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            var json = objectMapper.readTree(response);
            assertEquals("testuser", json.get("username").asText());
            assertEquals("test@example.com", json.get("email").asText());
        }

        @Test
        @DisplayName("Should return BadRequest if user not found")
        void shouldReturnBadRequestIfUserNotFound() throws Exception {
            mockMvc.perform(get("/api/user/testuser123")
                            .cookie(userCookie))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Update user tests")
    class UpdateUserTests {
        @Test
        @DisplayName("Should update user and return valid data")
        void updateUserShouldReturn200withValidData() throws Exception {
            var newFields = sampleUpdateUserDTO();

            var updatedUser = mockMvc.perform(put("/api/user/update-sensitive/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(userCookie)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(newFields.getUsername(), objectMapper.readTree(updatedUser).get("username").asText());
            assertEquals(newFields.getEmail(), objectMapper.readTree(updatedUser).get("email").asText());
        }

        @Test
        @DisplayName("Should return 401 BadRequest if new field has wrong format")
        void updateUserShouldReturn401BadRequest() throws Exception {
            var newFields = sampleUpdateUserDTO();
            newFields.setUsername("wr");

            mockMvc.perform(put("/api/user/update-sensitive/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(userCookie)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("Should return 401 Unauthorised if tokens is not valid")
        void updateUserShouldReturn401Unauthorised() throws Exception {
            MockCookie invalidCookie = new MockCookie("accessToken", "InvalidToken123123");
            var newFields = sampleUpdateUserDTO();
            mockMvc.perform(put("/api/user/update-sensitive/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(invalidCookie)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Update user by admin tests")
    class UpdateUserByAdminTests {
        @Test
        @DisplayName("Should update user and return valid data")
        void updateUserShouldReturn200withValidData() throws Exception {
            var newFields = sampleUpdateUserDTO();
            var updatedUser = mockMvc.perform(put("/api/user/update-sensitive/testuser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(adminCookie)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertEquals(objectMapper.readTree(updatedUser).get("username").asText(), newFields.getUsername());
            assertEquals(objectMapper.readTree(updatedUser).get("email").asText(), newFields.getEmail());
        }

        @Test
        @DisplayName("Should return 400 BadRequest if new field has wrong format")
        void updateUserShouldReturn400BadRequest() throws Exception {
            var newFields = sampleUpdateUserDTO();
            newFields.setUsername("wr");
            mockMvc.perform(put("/api/user/update-sensitive/testuser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(adminCookie)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 401 Unauthorised if tokens is not valid")
        void updateUserShouldReturn401Unauthorised() throws Exception {
            MockCookie invalidCookie = new MockCookie("accessToken", "InvalidToken123123");
            var newFields = sampleUpdateUserDTO();
            mockMvc.perform(put("/api/user/update-sensitive/testuser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(invalidCookie)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Delete user tests")
    class DeleteUserTests {
        @Test
        @DisplayName("Should delete user and return 200")
        void deleteUserShouldReturn200() throws Exception {
            mockMvc.perform(delete("/api/user/")
                            .cookie(userCookie))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 401 Unauthorised")
        void deleteUserShouldReturn401Unauthorised() throws Exception {
            MockCookie invalidCookie = new MockCookie("accessToken", "InvalidToken123123");
            mockMvc.perform(delete("/api/user/")
                            .cookie(invalidCookie))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Admin delete user tests")
    class DeleteUserAdminTests {
        @Test
        @DisplayName("Should delete user and return 200")
        void deleteUserShouldReturn200() throws Exception {
            mockMvc.perform(delete("/api/user/testuser")
                            .cookie(adminCookie))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 401 Unauthorised")
        void deleteUserShouldReturn401Unauthorised() throws Exception {
            MockCookie invalidCookie = new MockCookie("accessToken", "InvalidToken123123");
            mockMvc.perform(delete("/api/user/testuser")
                            .cookie(invalidCookie))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Admin should delete own account when calling DELETE /api/user/")
        void deleteUserShouldReturn200WhenAdminDeletesOwnAccount() throws Exception {
            mockMvc.perform(delete("/api/user/")
                            .cookie(adminCookie))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Role change tests")
    class RoleChangeTests {
        @Test
        @DisplayName("should return user data and set an ADMIN role")
        void roleChangeToAdminShouldReturn200() throws Exception {
            String response = mockMvc.perform(put("/api/user/role?role=ADMIN&username=testuser")
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            String role = mockMvc.perform(get("/api/user/role/")
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertEquals("testuser", objectMapper.readTree(response).get("username").asText());
            assertEquals("test@example.com", objectMapper.readTree(response).get("email").asText());
            assertEquals("ADMIN", objectMapper.readTree(role).asText());
        }

        @Test
        @DisplayName("should return user data and set a USER Role")
        void roleChangeToUserShouldReturn200() throws Exception {
            String response = mockMvc.perform(put("/api/user/role?role=USER&username=admin")
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            String role = mockMvc.perform(get("/api/user/role/")
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertEquals("admin", objectMapper.readTree(response).get("username").asText());
            assertEquals("admin@example.com", objectMapper.readTree(response).get("email").asText());
            assertEquals("USER", objectMapper.readTree(role).asText());
        }

        @Test
        @DisplayName("should return 500 forbidden if accessed not by admin")
        void shouldReturn500IfNotAdmin() throws Exception {
            mockMvc.perform(put("/api/user/role?role=ADMIN&username=testuser")
                            .cookie(userCookie))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Get user role tests")
    class GetUserRoleTests {
        @Test
        @DisplayName("Should return user role by username")
        void shouldReturnUserRoleByUsername() throws Exception {
            String response = mockMvc.perform(get("/api/user/role/testuser")
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertEquals("USER", objectMapper.readTree(response).asText());
        }

        @Test
        @DisplayName("Should return current user role if username is blank")
        void shouldReturnCurrentUserRoleIfUsernameIsBlank() throws Exception {
            String response = mockMvc.perform(get("/api/user/role/")
                            .cookie(userCookie))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertEquals("USER", objectMapper.readTree(response).asText());
        }

        @Test
        @DisplayName("Should return 401 if token is invalid")
        void shouldReturn401IfTokenInvalid() throws Exception {
            MockCookie invalidCookie = new MockCookie("accessToken", "invalidToken");
            mockMvc.perform(get("/api/user/role/testuser")
                            .cookie(invalidCookie))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 404 if user not found")
        void shouldReturn404IfUserNotFound() throws Exception {
            mockMvc.perform(get("/api/user/role/unknownuser")
                            .cookie(adminCookie))
                    .andExpect(status().isNotFound());
        }
    }

    private UpdateUserDTO sampleUpdateUserDTO() {
        return new UpdateUserDTO("updatedUsername",
                "updated@example.com",
                "updatedPassword123!");
    }

}
