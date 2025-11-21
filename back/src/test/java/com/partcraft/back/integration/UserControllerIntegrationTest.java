package com.partcraft.back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.User.UpdateUserDTO;
import com.partcraft.back.integration.helper.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("UserController Integration Tests")
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Nested
    @DisplayName("Get user tests")
    class GetUserTests {
        @Test
        @DisplayName("Should return user data")
        void shouldFindUserAndReturn200withValidData() throws Exception {
            String token = testUtils.createUser();

            String response = mockMvc.perform(get("/api/user/")
                            .header("Authorization", "Bearer " + token))
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
            String token = "invalidToken12345";
            testUtils.createUser();

            mockMvc.perform(get("/api/user/")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(get("/api/user/testuser")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return user data by username")
        void shouldFindUserByUsernameAndReturn200withValidData() throws Exception {
            String token = testUtils.createUser();

            String response = mockMvc.perform(get("/api/user/testuser")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            var json = objectMapper.readTree(response);
            assertEquals("testuser", json.get("username").asText());
            assertEquals("test@example.com", json.get("email").asText());
        }

        @Test
        @DisplayName("Should return BadRequest if user not found")
        void shouldReturnBadRequestIfUserNotFound() throws Exception {
            String token = testUtils.createUser();

            mockMvc.perform(get("/api/user/testuser123")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Update user tests")
    class UpdateUserTests {
        @Test
        @DisplayName("Should update user and return valid data")
        void updateUserShouldReturn200withValidData() throws Exception {
            String token = testUtils.createUser();
            var newFields = sampleUpdateUserDTO();

            var updatedUser = mockMvc.perform(put("/api/user/update-sensitive/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(newFields.getUsername(), objectMapper.readTree(updatedUser).get("username").asText());
            assertEquals(newFields.getEmail(), objectMapper.readTree(updatedUser).get("email").asText());
        }

        @Test
        @DisplayName("Should return 401 BadRequest if new field has wrong format")
        void updateUserShouldReturn401BadRequest() throws Exception {
            String token = testUtils.createUser();
            var newFields = sampleUpdateUserDTO();
            newFields.setUsername("wr");

            mockMvc.perform(put("/api/user/update-sensitive/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("Should return 401 Unauthorised if tokens is not valid")
        void updateUserShouldReturn401Unauthorised() throws Exception {
            String token = "InvalidToken123123";
            var newFields = sampleUpdateUserDTO();

            mockMvc.perform(put("/api/user/update-sensitive/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
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
            String token = testUtils.createAdmin();
            testUtils.createUser();
            var newFields = sampleUpdateUserDTO();

            var updatedUser = mockMvc.perform(put("/api/user/update-sensitive/testuser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(objectMapper.readTree(updatedUser).get("username").asText(), newFields.getUsername());
            assertEquals(objectMapper.readTree(updatedUser).get("email").asText(), newFields.getEmail());
        }

        @Test
        @DisplayName("Should return 400 BadRequest if new field has wrong format")
        void updateUserShouldReturn400BadRequest() throws Exception {
            String token = testUtils.createAdmin();
            testUtils.createUser();
            var newFields = sampleUpdateUserDTO();
            newFields.setUsername("wr");

            mockMvc.perform(put("/api/user/update-sensitive/testuser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(newFields)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 401 Unauthorised if tokens is not valid")
        void updateUserShouldReturn401Unauthorised() throws Exception {
            String token = "InvalidToken123123";
            var newFields = sampleUpdateUserDTO();

            mockMvc.perform(put("/api/user/update-sensitive/testuser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
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
            String token = testUtils.createUser();

            mockMvc.perform(delete("/api/user/")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 401 Unauthorised")
        void deleteUserShouldReturn401Unauthorised() throws Exception {
            String token = "InvalidToken123123";

            mockMvc.perform(delete("/api/user/")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Admin delete user tests")
    class DeleteUserAdminTests {
        @Test
        @DisplayName("Should delete user and return 200")
        void deleteUserShouldReturn200() throws Exception {
            String token = testUtils.createAdmin();
            testUtils.createUser();

            mockMvc.perform(delete("/api/user/testuser")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 401 Unauthorised")
        void deleteUserShouldReturn401Unauthorised() throws Exception {
            String token = "InvalidToken123123";

            mockMvc.perform(delete("/api/user/testuser")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Admin should delete own account when calling DELETE /api/user/")
        void deleteUserShouldReturn200WhenAdminDeletesOwnAccount() throws Exception {
            String token = testUtils.createAdmin();

            mockMvc.perform(delete("/api/user/")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Role change tests")
    class RoleChangeTests {
        @Test
        @DisplayName("should return user data and set an ADMIN role")
        void roleChangeToAdminShouldReturn200() throws Exception {
            String token = testUtils.createAdmin();
            testUtils.createUser();

            String response = mockMvc.perform(put("/api/user/role?role=ADMIN&username=testuser")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String role = mockMvc.perform(get("/api/user/role/testuser")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertEquals("testuser", objectMapper.readTree(response).get("username").asText());
            assertEquals("test@example.com", objectMapper.readTree(response).get("email").asText());
            assertEquals("ADMIN", objectMapper.readTree(role).asText());
        }

        @Test
        @DisplayName("should return user data and set a USER Role")
        void roleChangeToUserShouldReturn200() throws Exception {
            String token = testUtils.createAdmin();
            testUtils.createUser();

            String response = mockMvc.perform(put("/api/user/role?role=USER&username=admin")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String role = mockMvc.perform(get("/api/user/role/")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertEquals("admin", objectMapper.readTree(response).get("username").asText());
            assertEquals("admin@example.com", objectMapper.readTree(response).get("email").asText());
            assertEquals("USER", objectMapper.readTree(role).asText());
        }

        @Test
        @DisplayName("should return 500 forbidden if accessed not by admin")
        void shouldReturn500IfNotAdmin() throws Exception {
            String token = testUtils.createUser();
            mockMvc.perform(put("/api/user/role?role=ADMIN&username=testuser")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Get user role tests")
    class GetUserRoleTests {
        @Test
        @DisplayName("Should return user role by username")
        void shouldReturnUserRoleByUsername() throws Exception {
            String adminToken = testUtils.createAdmin();
            testUtils.createUser();

            String response = mockMvc.perform(get("/api/user/role/testuser")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertEquals("USER", objectMapper.readTree(response).asText());
        }

        @Test
        @DisplayName("Should return current user role if username is blank")
        void shouldReturnCurrentUserRoleIfUsernameIsBlank() throws Exception {
            String userToken = testUtils.createUser();

            String response = mockMvc.perform(get("/api/user/role/")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertEquals("USER", objectMapper.readTree(response).asText());
        }

        @Test
        @DisplayName("Should return 401 if token is invalid")
        void shouldReturn401IfTokenInvalid() throws Exception {
            mockMvc.perform(get("/api/user/role/testuser")
                            .header("Authorization", "Bearer invalidToken"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 404 if user not found")
        void shouldReturn404IfUserNotFound() throws Exception {
            String adminToken = testUtils.createAdmin();

            mockMvc.perform(get("/api/user/role/unknownuser")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }
    }

    private UpdateUserDTO sampleUpdateUserDTO() {
        return new UpdateUserDTO("updatedUsername",
                "updated@example.com",
                "updatedPassword123!");
    }

}
