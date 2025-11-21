package com.partcraft.back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.componentDTO.CPUDTO;
import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.integration.helper.TestUtils;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.repository.component.CPURepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Generic test for ComponentController using CPU as the concrete implementation.
 * This test covers all component controllers that extend ComponentController
 * (CPU, GPU, RAM, Storage, etc.) since they all share the same base functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Components Controller Integration Tests")
class ComponentControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CPURepository cpuRepository;

    @Autowired
    private TestUtils testUtils;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Delete components first before users to avoid FK constraint violations
        cpuRepository.deleteAll();
        userRepository.deleteAll();
        adminToken = testUtils.createAdmin();
        userToken = testUtils.createUser();
    }

    // Using CPU endpoints as representative of all component controllers

    private static final String COMPONENT_BASE_URL = "/api/cpu";

    @Nested
    @DisplayName("Create component tests")
    class CreateComponentTests {


        @Test
        @DisplayName("Admin should create component successfully")
        void adminShouldCreateComponent() throws Exception {
            var componentDTO = sampleCPUDTO();

            mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cpuModel").value("Intel i9-13900K"));
        }

        @Test
        @DisplayName("User should get 403 Forbidden when trying to create component")
        void userShouldGet403WhenCreatingComponent() throws Exception {
            var componentDTO = sampleCPUDTO();

            mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            var componentDTO = sampleCPUDTO();

            mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Update component tests")
    class UpdateComponentTests {

        @Test
        @DisplayName("Admin should update component successfully")
        void adminShouldUpdateComponent() throws Exception {

            // Create component first
            var componentDTO = sampleCPUDTO();
            String response = mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long componentId = objectMapper.readTree(response).get("id").asLong();

            // Update the component
            componentDTO.setCpuModel("Intel i9-14900K");
            mockMvc.perform(put(COMPONENT_BASE_URL + "/" + componentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cpuModel").value("Intel i9-14900K"));
        }

        @Test
        @DisplayName("User should get 403 Forbidden when trying to update component")
        void userShouldGet403WhenUpdatingComponent() throws Exception {

            // Create component as admin
            var componentDTO = sampleCPUDTO();
            String response = mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long componentId = objectMapper.readTree(response).get("id").asLong();

            // Try to update as user
            componentDTO.setCpuModel("Intel i9-14900K");
            mockMvc.perform(put(COMPONENT_BASE_URL + "/" + componentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Delete component tests")
    class DeleteComponentTests {

        @Test
        @DisplayName("Admin should delete component successfully")
        void adminShouldDeleteComponent() throws Exception {

            // Create component first
            var componentDTO = sampleCPUDTO();
            String response = mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long componentId = objectMapper.readTree(response).get("id").asLong();

            // Delete the component
            mockMvc.perform(delete(COMPONENT_BASE_URL + "/" + componentId)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());

            // Verify it's deleted
            mockMvc.perform(get(COMPONENT_BASE_URL + "/" + componentId)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("User should get 403 Forbidden when trying to delete component")
        void userShouldGet403WhenDeletingComponent() throws Exception {

            // Create component as admin
            var componentDTO = sampleCPUDTO();
            String response = mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long componentId = objectMapper.readTree(response).get("id").asLong();

            // Try to delete as user
            mockMvc.perform(delete(COMPONENT_BASE_URL + "/" + componentId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Find by ID tests")
    class FindByIdTests {

        @Test
        @DisplayName("User should get component by ID successfully")
        void userShouldGetComponentById() throws Exception {

            // Create component as admin
            var componentDTO = sampleCPUDTO();
            String response = mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(componentDTO)))
                    .andReturn().getResponse().getContentAsString();

            Long componentId = objectMapper.readTree(response).get("id").asLong();

            // Get as user
            mockMvc.perform(get(COMPONENT_BASE_URL + "/" + componentId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cpuModel").value("Intel i9-13900K"));
        }

        @Test
        @DisplayName("Should return 404 when component not found")
        void shouldReturn404WhenNotFound() throws Exception {

            mockMvc.perform(get(COMPONENT_BASE_URL + "/99999")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            mockMvc.perform(get(COMPONENT_BASE_URL + "/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Find all tests")
    class FindAllTests {

        @Test
        @DisplayName("User should get all components successfully")
        void userShouldGetAllComponents() throws Exception {

            // Create multiple components
            var component1 = sampleCPUDTO();
            mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(component1)))
                    .andExpect(status().isOk());

            var component2 = sampleCPUDTO();
            component2.setCpuModel("AMD Ryzen 9 7950X");
            mockMvc.perform(post(COMPONENT_BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(component2)))
                    .andExpect(status().isOk());

            // Get all as user
            mockMvc.perform(get(COMPONENT_BASE_URL)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Should return empty list when no components exist")
        void shouldReturnEmptyListWhenNoComponents() throws Exception {

            mockMvc.perform(get(COMPONENT_BASE_URL)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    private CPUDTO sampleCPUDTO() {
        CPUDTO dto = new CPUDTO();
        dto.setCpuModel("Intel i9-13900K");
        dto.setCpuBrand("Intel");
        dto.setCpuCores(24);
        dto.setCpuThreads(32);
        dto.setCpuBaseClockGhz(3.0);
        dto.setCpuBoostClockGhz(5.8);
        dto.setPowerDraw(253);
        dto.setSize(new Size(45.0, 34.0, 23.0));
        dto.setPictureUrl("https://example.com/cpu.jpg");
        return dto;
    }
}

