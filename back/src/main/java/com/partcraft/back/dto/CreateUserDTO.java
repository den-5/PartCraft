package com.partcraft.back.dto;

import lombok.Data;

@Data
public class CreateUserDTO {
    private String userId;
    private String username;
    private String email;
    private String password;
}
