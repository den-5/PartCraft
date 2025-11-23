package com.partcraft.back.dto;

import com.partcraft.back.dto.User.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private UserDTO user;
    private JwtTokensDTO tokens; // Only for internal use, not exposed in API response
}
