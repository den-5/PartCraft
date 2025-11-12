package com.partcraft.back.integration.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createUser() throws Exception {
        var request = new CreateUserDTO(
                "testuser",
                "test@example.com",
                "Password123!"
        );

        String response = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("tokens").path("accessToken").asText();
    }

    public String createAdmin() throws Exception {
        var request = new CreateUserDTO(
                "admin",
                "admin@example.com",
                "AdminPassword123!"
        );

        var admin = new User();
        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        return objectMapper.readTree(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString()).path("tokens").path("accessToken").asText();
    }
}
