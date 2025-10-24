package com.partcraft.back.dto;

import lombok.Data;

@Data
public class JwtTokensDTO {
    private String refreshToken;
    private String accessToken;

}
