package com.partcraft.back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.ComponentPriceDTO;
import com.partcraft.back.enums.Location;
import com.partcraft.back.integration.helper.TestUtils;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("ComponentPriceController Integration Tests")
public class ComponentPriceControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    private static final String BASE_URL = "/api/component/price";

    private ComponentPriceDTO samplePriceDTO() {
        return new ComponentPriceDTO(
                null,
                199.99,
                LocalDate.now(),
                Location.RU,
                "CPU",
                100L
        );
    }

    @Nested
    @DisplayName("Create price tests")
    class CreatePriceTests {
        @Test
        @DisplayName("Admin should create price successfully")
        void adminShouldCreatePrice() throws Exception {
            String adminToken = testUtils.createAdmin();
            var dto = samplePriceDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.value").value(199.99))
                    .andExpect(jsonPath("$.componentType").value("CPU"));
        }

        @Test
        @DisplayName("User should get 403 Forbidden when creating price")
        void userShouldGet403WhenCreatingPrice() throws Exception {
            String userToken = testUtils.createUser();
            var dto = samplePriceDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Unauthenticated request should return 401")
        void unauthenticatedShouldGet401() throws Exception {
            var dto = samplePriceDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Update price tests")
    class UpdatePriceTests {
        @Test
        @DisplayName("Admin should update price successfully")
        void adminShouldUpdatePrice() throws Exception {
            String adminToken = testUtils.createAdmin();
            var dto = samplePriceDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long priceId = objectMapper.readTree(response).get("id").asLong();
            dto.setValue(149.99);
            mockMvc.perform(put(BASE_URL + "/" + priceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.value").value(149.99));
        }

        @Test
        @DisplayName("User should get 403 Forbidden when updating price")
        void userShouldGet403WhenUpdatingPrice() throws Exception {
            String adminToken = testUtils.createAdmin();
            String userToken = testUtils.createUser();
            var dto = samplePriceDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long priceId = objectMapper.readTree(response).get("id").asLong();
            dto.setValue(149.99);
            mockMvc.perform(put(BASE_URL + "/" + priceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + userToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Delete price tests")
    class DeletePriceTests {
        @Test
        @DisplayName("Admin should delete price successfully")
        void adminShouldDeletePrice() throws Exception {
            String adminToken = testUtils.createAdmin();
            var dto = samplePriceDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long priceId = objectMapper.readTree(response).get("id").asLong();
            mockMvc.perform(delete(BASE_URL + "/" + priceId)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get(BASE_URL + "/" + priceId)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("User should get 403 Forbidden when deleting price")
        void userShouldGet403WhenDeletingPrice() throws Exception {
            String adminToken = testUtils.createAdmin();
            String userToken = testUtils.createUser();
            var dto = samplePriceDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long priceId = objectMapper.readTree(response).get("id").asLong();
            mockMvc.perform(delete(BASE_URL + "/" + priceId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Find by ID tests")
    class FindByIdTests {
        @Test
        @DisplayName("User should get price by ID successfully")
        void userShouldGetPriceById() throws Exception {
            String adminToken = testUtils.createAdmin();
            String userToken = testUtils.createUser();
            var dto = samplePriceDTO();
            String response = mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andReturn().getResponse().getContentAsString();
            Long priceId = objectMapper.readTree(response).get("id").asLong();
            mockMvc.perform(get(BASE_URL + "/" + priceId)
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.value").value(199.99));
        }

        @Test
        @DisplayName("Should return 404 when price not found")
        void shouldReturn404WhenNotFound() throws Exception {
            String userToken = testUtils.createUser();
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
        @DisplayName("User should get all prices for a component successfully")
        void userShouldGetAllPrices() throws Exception {
            String adminToken = testUtils.createAdmin();
            String userToken = testUtils.createUser();
            var dto1 = samplePriceDTO();
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto1)))
                    .andExpect(status().isOk());
            var dto2 = samplePriceDTO();
            dto2.setValue(149.99);
            dto2.setTime(LocalDate.now().minusDays(1)); // Different date to avoid unique constraint violation
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminToken)
                            .content(objectMapper.writeValueAsString(dto2)))
                    .andExpect(status().isOk());
            mockMvc.perform(get(BASE_URL + "/history?componentId=100&componentType=CPU")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
        }

        @Test
        @DisplayName("Should return 404 when no prices exist for component")
        void shouldReturn404WhenNoPrices() throws Exception {
            String userToken = testUtils.createUser();
            mockMvc.perform(get(BASE_URL + "/history?componentId=99999&componentType=CPU")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isNotFound());
        }
    }
}
