package com.partcraft.back.integration.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.entity.component.CPU;
import com.partcraft.back.entity.component.MotherBoard;
import com.partcraft.back.enums.UserRole;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.repository.component.CPURepository;
import com.partcraft.back.repository.component.MotherBoardRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockCookie;

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

    @Autowired
    private CPURepository cpuRepository;
    @Autowired
    private MotherBoardRepository motherBoardRepository;

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

    public Long createCpu(String socketType) {
        CPU cpu = new CPU();
        cpu.setCpuBrand("TestBrand");
        cpu.setCpuModel("TestModel");
        cpu.setCpuCores(4);
        cpu.setCpuThreads(8);
        cpu.setCpuSocketType(socketType);
        cpu.setPowerDraw(65);
        return cpuRepository.save(cpu).getId();
    }

    public Long createMotherboard(String socketType) {
        MotherBoard mb = new MotherBoard();
        mb.setMotherboardBrand("TestBrand");
        mb.setMotherboardModel("TestModel");
        mb.setChipset("TestChipset");
        mb.setSocketType(socketType);
        mb.setMemoryType("DDR4");
        mb.setPowerDraw(50);
        return motherBoardRepository.save(mb).getId();
    }

    public MockCookie createUserAndGetAccessCookie() throws Exception {
        var request = new CreateUserDTO(
                "testuser",
                "test@example.com",
                "Password123!"
        );
        MvcResult result = mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        // Print the actual error if the status is not 200 OK
        if (result.getResponse().getStatus() >= 400) {
            throw new RuntimeException("Sign-up failed with status " + result.getResponse().getStatus() +
                " | Response Body: " + result.getResponse().getContentAsString());
        }

        Cookie[] cookies = result.getResponse().getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("accessToken")) {
                    return new MockCookie("accessToken", c.getValue());
                }
            }
        }
        throw new RuntimeException("No accessToken cookie found. Response was 200 but no cookie set.");
    }

    public MockCookie createAdminAndGetAccessCookie() throws Exception {
        var request = new CreateUserDTO(
                "admin",
                "admin@example.com",
                "AdminPassword123!"
        );
        // Safe check to avoid unique constraint errors if tests run in parallel
        if (userRepository.findUserByUsername(request.getUsername()).isEmpty()) {
            var admin = new User();
            admin.setUsername(request.getUsername());
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        if (result.getResponse().getStatus() >= 400) {
            throw new RuntimeException("Login failed with status " + result.getResponse().getStatus() +
                " | Response Body: " + result.getResponse().getContentAsString());
        }

        Cookie[] cookies = result.getResponse().getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("accessToken")) {
                    return new MockCookie("accessToken", c.getValue());
                }
            }
        }
        throw new RuntimeException("No accessToken cookie found. Response was 200 but no cookie set.");
    }
}
