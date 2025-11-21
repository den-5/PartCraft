package com.partcraft.back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.ComponentLinkDTO;
import com.partcraft.back.integration.helper.TestUtils;
import com.partcraft.back.repository.ComponentLinkRepository;
import com.partcraft.back.repository.UserRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ComponentLinkController Integration Tests")
public class ComponentLinkControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComponentLinkRepository componentLinkRepository;

    private static final String BASE_URL = "/api/component/link";

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Delete component links first before users to avoid FK constraint violations
        componentLinkRepository.deleteAll();
        userRepository.deleteAll();
        // Create shared users once
        adminToken = testUtils.createAdmin();
        userToken = testUtils.createUser();
    }

    private ComponentLinkDTO sampleLinkDTO() {
        return new ComponentLinkDTO(
                null,
                "CPU",
                100L,
                "http://example.com"
        );
    }

    @Nested
    @DisplayName("Create link tests")
    class CreateLinkTests {
        @Test
        @DisplayName("Admin should create link successfully")
        void adminShouldCreateLink() throws Exception {
            var dto = sampleLinkDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.componentType").value("CPU"))
                    .andExpect(jsonPath("$.url").value("http://example.com"));
        }

        @Test
        @DisplayName("User should get 403 Forbidden when creating link")
        void userShouldGet403WhenCreatingLink() throws Exception {
            var dto = sampleLinkDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            var dto = sampleLinkDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Update link tests")
    class UpdateLinkTests {
        @Test
        @DisplayName("Admin should update link successfully")
        void adminShouldUpdateLink() throws Exception {
            var dto = sampleLinkDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long linkId = objectMapper.readTree(response).get("id").asLong();
            dto.setUrl("http://updated.com");
            mockMvc.perform(put(BASE_URL + "/" + linkId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.url").value("http://updated.com"));
        }

        @Test
        @DisplayName("User should get 403 Forbidden when updating link")
        void userShouldGet403WhenUpdatingLink() throws Exception {
            var dto = sampleLinkDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long linkId = objectMapper.readTree(response).get("id").asLong();
            dto.setUrl("http://updated.com");
            mockMvc.perform(put(BASE_URL + "/" + linkId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Delete link tests")
    class DeleteLinkTests {
        @Test
        @DisplayName("Admin should delete link successfully")
        void adminShouldDeleteLink() throws Exception {
            var dto = sampleLinkDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long linkId = objectMapper.readTree(response).get("id").asLong();
            mockMvc.perform(delete(BASE_URL + "/" + linkId)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get(BASE_URL + "/" + linkId)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("User should get 403 Forbidden when deleting link")
        void userShouldGet403WhenDeletingLink() throws Exception {
            var dto = sampleLinkDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long linkId = objectMapper.readTree(response).get("id").asLong();
            mockMvc.perform(delete(BASE_URL + "/" + linkId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Find by ID tests")
    class FindByIdTests {
        @Test
        @DisplayName("User should get link by ID successfully")
        void userShouldGetLinkById() throws Exception {
            var dto = sampleLinkDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long linkId = objectMapper.readTree(response).get("id").asLong();
            mockMvc.perform(get(BASE_URL + "/" + linkId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.url").value("http://example.com"));
        }

        @Test
        @DisplayName("Should return 404 when link not found")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(get(BASE_URL + "/99999")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            mockMvc.perform(get(BASE_URL + "/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Find all (history) tests")
    class FindAllTests {
        @Test
        @DisplayName("User should get all links for a component successfully")
        void userShouldGetAllLinks() throws Exception {
            var dto1 = sampleLinkDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto1)))
                    .andExpect(status().isOk());
            var dto2 = sampleLinkDTO();
            dto2.setUrl("http://another.com");
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto2)))
                    .andExpect(status().isOk());
            mockMvc.perform(get(BASE_URL + "/all?componentId=100&componentType=CPU")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
        }

        @Test
        @DisplayName("Should return 404 when no links exist for component")
        void shouldReturn404WhenNoLinks() throws Exception {
            mockMvc.perform(get(BASE_URL + "/all?componentId=99999&componentType=CPU")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }
    }
}
