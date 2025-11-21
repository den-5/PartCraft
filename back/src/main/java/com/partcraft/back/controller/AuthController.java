package com.partcraft.back.controller;

import com.partcraft.back.dto.*;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.exception.AuthException;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.RefreshTokenService;
import com.partcraft.back.service.UserService;
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


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) throws AuthException {

        var authResponseDTO = userService.getUserByEmail(request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authResponseDTO.getUser().getUsername(),
                            request.getPassword())
            );
        } catch (Exception e) {
            throw new AuthException("Invalid email or password");
        }
        return ResponseEntity.ok(authResponseDTO);
    }
    //test pipeline

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDTO> signUp(@RequestBody CreateUserDTO request) {
        var authResponseDTO = userService.createUser(request);
        return ResponseEntity.ok(authResponseDTO);
    }

    @GetMapping("/refresh")
    public ResponseEntity<JwtTokensDTO> refresh(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws AuthException {
        try {
            String refreshToken = null;
            if (authorizationHeader == null) {
                throw new AuthException("Refresh token required");
            }
            if (!authorizationHeader.startsWith("Bearer ")) {
                throw new AuthException("Invalid authorization header format");
            }

            refreshToken = authorizationHeader.substring(7);

            if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
                String username = jwtUtils.getUsernameFromRefreshToken(refreshToken);
                refreshTokenService.deleteRefreshToken(refreshToken);

                return ResponseEntity.ok().body(new JwtTokensDTO(jwtUtils.generateToken(username),
                        refreshTokenService.createRefreshToken(username)));
            }

            throw new AuthException("Invalid refresh token");
        } catch (Exception e) {
            throw new AuthException("Error while validating refresh token");
        }
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