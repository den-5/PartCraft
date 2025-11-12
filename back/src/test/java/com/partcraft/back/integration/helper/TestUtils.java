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
        mb.setPowerDraw(50);
        return motherBoardRepository.save(mb).getId();
    }
}
