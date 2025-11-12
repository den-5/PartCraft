package com.partcraft.back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.PC.CreatePCDTO;
import com.partcraft.back.dto.PC.UpdatePCDTO;
import com.partcraft.back.integration.helper.TestUtils;
import com.partcraft.back.enums.VisibilityState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("PC controller integration tests")
public class PCControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    private static final String PC_BASE_URL = "/api/pc";

    @Nested
    @DisplayName("Create PC tests")
    class CreatePCTests {

        @Test
        @DisplayName("User should create PC successfully")
        void userShouldCreatePC() throws Exception {
            String userToken = testUtils.createUser();
            var createPCDTO = sampleCreatePCDTO();

            String response = mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(createPCDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Gaming PC"))
                    .andExpect(jsonPath("$.description").value("High-end gaming build"))
                    .andExpect(jsonPath("$.purpose").value("Gaming"))
                    .andExpect(jsonPath("$.visibility").value("PUBLIC"))
                    .andReturn().getResponse().getContentAsString();

            assertNotNull(objectMapper.readTree(response).get("id"));
            assertNotNull(objectMapper.readTree(response).get("ownerId"));
        }

        @Test
        @DisplayName("Admin should create PC successfully")
        void adminShouldCreatePC() throws Exception {
            String adminToken = testUtils.createAdmin();
            var createPCDTO = sampleCreatePCDTO();

            mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(createPCDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Gaming PC"));
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            var createPCDTO = sampleCreatePCDTO();

            mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPCDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Get PC by ID tests")
    class GetPCByIdTests {

        @Test
        @DisplayName("User should get PC by ID successfully")
        void userShouldGetPCById() throws Exception {
            String userToken = testUtils.createUser();
            var createPCDTO = sampleCreatePCDTO();

            // Create PC first
            String createResponse = mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(createPCDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long pcId = objectMapper.readTree(createResponse).get("id").asLong();

            // Get PC by ID
            mockMvc.perform(get(PC_BASE_URL + "/" + pcId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(pcId))
                    .andExpect(jsonPath("$.name").value("Gaming PC"));
        }

        @Test
        @DisplayName("Should return 404 when PC not found")
        void shouldReturn404WhenPCNotFound() throws Exception {
            String userToken = testUtils.createUser();

            mockMvc.perform(get(PC_BASE_URL + "/99999")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            mockMvc.perform(get(PC_BASE_URL + "/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Get user PCs tests")
    class GetUserPCsTests {

        @Test
        @DisplayName("Should get all user's PCs successfully")
        void shouldGetAllUserPCs() throws Exception {
            String userToken = testUtils.createUser();

            // Create multiple PCs
            var pc1 = sampleCreatePCDTO();
            mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(pc1)))
                    .andExpect(status().isOk());

            var pc2 = sampleCreatePCDTO();
            pc2.setName("Workstation PC");
            pc2.setPurpose("Work");
            mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(pc2)))
                    .andExpect(status().isOk());

            // Get all PCs for user
            mockMvc.perform(get(PC_BASE_URL + "/user/testuser")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            String userToken = testUtils.createUser();

            mockMvc.perform(get(PC_BASE_URL + "/user/nonexistentuser")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 200 and empty array when user has no PCs")
        void shouldReturn200WithEmptyArrayWhenUserHasNoPCs() throws Exception {
            String userToken = testUtils.createUser();

            mockMvc.perform(get(PC_BASE_URL + "/user/testuser")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            mockMvc.perform(get(PC_BASE_URL + "/user/testuser"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Update PC fields tests")
    class UpdatePCFieldsTests {

        @Test
        @DisplayName("User should update own PC fields successfully")
        void userShouldUpdateOwnPCFields() throws Exception {
            String userToken = testUtils.createUser();
            var createPCDTO = sampleCreatePCDTO();

            // Create PC
            String createResponse = mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(createPCDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long pcId = objectMapper.readTree(createResponse).get("id").asLong();

            // Update PC fields
            var updateDTO = sampleUpdatePCDTO();
            updateDTO.setName("Updated Gaming PC");
            updateDTO.setDescription("Updated description");
            updateDTO.setTags(Arrays.asList("gaming", "rgb", "high-end"));

            mockMvc.perform(put(PC_BASE_URL + "/update-fields/" + pcId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Gaming PC"))
                    .andExpect(jsonPath("$.description").value("Updated description"))
                    .andExpect(jsonPath("$.tags.length()").value(3));
        }

        @Test
        @DisplayName("Should return 404 when PC not found")
        void shouldReturn404WhenPCNotFound() throws Exception {
            String userToken = testUtils.createUser();
            var updateDTO = sampleUpdatePCDTO();

            mockMvc.perform(put(PC_BASE_URL + "/update-fields/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            var updateDTO = sampleUpdatePCDTO();

            mockMvc.perform(put(PC_BASE_URL + "/update-fields/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Update PC components tests")
    class UpdatePCComponentsTests {

        @Test
        @DisplayName("User should update own PC components successfully")
        void userShouldUpdateOwnPCComponents() throws Exception {
            String userToken = testUtils.createUser();
            var createPCDTO = sampleCreatePCDTO();

            // Create PC
            String createResponse = mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(createPCDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long pcId = objectMapper.readTree(createResponse).get("id").asLong();

            // Update PC components
            var updateDTO = sampleUpdatePCDTO();

            mockMvc.perform(put(PC_BASE_URL + "/update-components/" + pcId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 when PC not found")
        void shouldReturn404WhenPCNotFound() throws Exception {
            String userToken = testUtils.createUser();
            var updateDTO = sampleUpdatePCDTO();

            mockMvc.perform(put(PC_BASE_URL + "/update-components/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            var updateDTO = sampleUpdatePCDTO();

            mockMvc.perform(put(PC_BASE_URL + "/update-components/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Delete PC tests")
    class DeletePCTests {

        @Test
        @DisplayName("User should delete own PC successfully")
        void userShouldDeleteOwnPC() throws Exception {
            String userToken = testUtils.createUser();
            var createPCDTO = sampleCreatePCDTO();

            // Create PC
            String createResponse = mockMvc.perform(post(PC_BASE_URL + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(createPCDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long pcId = objectMapper.readTree(createResponse).get("id").asLong();

            // Delete PC
            mockMvc.perform(delete(PC_BASE_URL + "/" + pcId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());

            // Verify PC is deleted
            mockMvc.perform(get(PC_BASE_URL + "/" + pcId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when PC not found")
        void shouldReturn404WhenPCNotFound() throws Exception {
            String userToken = testUtils.createUser();

            mockMvc.perform(delete(PC_BASE_URL + "/99999")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            mockMvc.perform(delete(PC_BASE_URL + "/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // Helper methods to create sample DTOs
    private CreatePCDTO sampleCreatePCDTO() {
        CreatePCDTO dto = new CreatePCDTO();
        dto.setName("Gaming PC");
        dto.setDescription("High-end gaming build");
        dto.setPurpose("Gaming");
        dto.setLocation("Home Office");
        dto.setVisibility(VisibilityState.PUBLIC);
        dto.setCpuId(null);
        dto.setGpuId(null);
        dto.setStorageId(null);
        dto.setRamKitId(null);
        dto.setPsuId(null);
        dto.setCoolerIds(null);
        dto.setCpuCoolerId(null);
        dto.setMotherboardId(null);
        dto.setPcCaseId(null);
        return dto;
    }

    private UpdatePCDTO sampleUpdatePCDTO() {
        UpdatePCDTO dto = new UpdatePCDTO();
        dto.setName("Updated PC");
        dto.setDescription("Updated description");
        dto.setPurpose("Work");
        dto.setVisibility(VisibilityState.PRIVATE);
        dto.setTags(Arrays.asList("tag1", "tag2"));
        dto.setCpuId(null);
        dto.setGpuId(null);
        dto.setStorageId(null);
        dto.setRamKitId(null);
        dto.setPsuId(null);
        dto.setCoolerIds(null);
        dto.setCpuCoolerId(null);
        dto.setMotherboardId(null);
        dto.setPcCaseId(null);
        return dto;
    }
}
