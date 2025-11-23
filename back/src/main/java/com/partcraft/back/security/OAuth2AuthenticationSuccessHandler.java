package com.partcraft.back.security;

import com.partcraft.back.service.UserService;
import com.partcraft.back.dto.AuthResponseDTO;
import com.partcraft.back.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public OAuth2AuthenticationSuccessHandler(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 1. Find or create user by Google ID, update email if needed
        User user = userService.findOrCreateGoogleUser(googleId, email, name);

        // 2. Generate tokens
        AuthResponseDTO authResponse = userService.generateTokensForUser(user);

        // 3. Set cookies (HttpOnly, not secure for localhost)
        Cookie accessTokenCookie = new Cookie("accessToken", authResponse.getTokens().getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(1800);
        response.addCookie(accessTokenCookie);
        Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getTokens().getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(86400);
        response.addCookie(refreshTokenCookie);

        // 4. Optionally, redirect or return user info as JSON
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(authResponse.getUser()));
    }
}
