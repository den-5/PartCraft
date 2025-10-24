package com.partcraft.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private UserDTO user;
    private JwtTokensDTO jwtTokensDTO;
}
