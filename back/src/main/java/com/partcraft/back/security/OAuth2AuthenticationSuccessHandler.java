package com.partcraft.back.security;

import com.partcraft.back.dto.AuthResponseDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value; // Added import
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    // Inject frontend URL from properties, default to localhost:3000 if not found
    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

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

        // 1. Find or create user by Google ID
        User user = userService.findOrCreateGoogleUser(googleId, email, name);

        // 2. Generate tokens
        AuthResponseDTO authResponse = userService.generateTokensForUser(user);

        // 3. Set cookies (HttpOnly, Lax, Secure=false for localhost)
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", authResponse.getTokens().getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(1800) // 30 minutes
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", authResponse.getTokens().getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(86400) // 1 day
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        // 4. REDIRECT to the frontend
        // This sends the browser back to your Next.js app with the cookies set
        response.sendRedirect(frontendUrl);
    }
}