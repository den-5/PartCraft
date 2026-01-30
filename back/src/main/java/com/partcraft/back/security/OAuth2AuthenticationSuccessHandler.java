package com.partcraft.back.security;

import com.partcraft.back.dto.AuthResponseDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.service.UserService;
import com.partcraft.back.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final CookieUtils cookieUtils;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(UserService userService, CookieUtils cookieUtils) {
        this.userService = userService;
        this.cookieUtils = cookieUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Find or create user by Google ID
        User user = userService.findOrCreateGoogleUser(googleId, email, name);

        // Generate tokens
        AuthResponseDTO authResponse = userService.generateTokensForUser(user);

        // Set cookies using centralized utility
        cookieUtils.setAuthCookies(response, authResponse.getTokens().getAccessToken(), authResponse.getTokens().getRefreshToken());

        // Redirect to the frontend
        response.sendRedirect(frontendUrl);
    }
}