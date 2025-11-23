package com.partcraft.back.controller;

import com.partcraft.back.dto.*;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.exception.AuthException;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.RefreshTokenService;
import com.partcraft.back.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(1800) // 30 min
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(86400) // 1 day
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) throws AuthException {

        var authResponseDTO = userService.getUserByEmail(request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authResponseDTO.getUser().getUsername(),
                            request.getPassword())
            );
        } catch (Exception e) {
            throw new AuthException("Invalid email or password");
        }
        setAuthCookies(response, authResponseDTO.getTokens().getAccessToken(), authResponseDTO.getTokens().getRefreshToken());
        return ResponseEntity.ok(authResponseDTO.getUser());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> signUp(@RequestBody CreateUserDTO request, HttpServletResponse response) {
        var authResponseDTO = userService.createUser(request);
        setAuthCookies(response, authResponseDTO.getTokens().getAccessToken(), authResponseDTO.getTokens().getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(authResponseDTO.getUser());
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletResponse response, @CookieValue(value = "refreshToken", required = false) String refreshToken) throws AuthException {
        if (refreshToken == null) {
            throw new AuthException("Refresh token required");
        }
        if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
            String username = jwtUtils.getUsernameFromRefreshToken(refreshToken);
            refreshTokenService.deleteRefreshToken(refreshToken);
            String newAccessToken = jwtUtils.generateToken(username);
            String newRefreshToken = refreshTokenService.createRefreshToken(username);
            setAuthCookies(response, newAccessToken, newRefreshToken);
            return ResponseEntity.ok().build();
        }
        throw new AuthException("Invalid refresh token");
    }

    @GetMapping("/username-availability/{username}")
    public ResponseEntity<Boolean> isUsernameAvailable(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.verifyUsernameAvailability(username));
    }

    @GetMapping("/email-availability/{email}")
    public ResponseEntity<Boolean> isEmailAvailable(@PathVariable String email) {
        return ResponseEntity.ok().body(userService.verifyEmailAvailability(email));
    }
}