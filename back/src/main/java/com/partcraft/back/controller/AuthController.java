package com.partcraft.back.controller;

import com.partcraft.back.dto.*;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.exception.AuthException;
import com.partcraft.back.exception.NotFoundException;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.RefreshTokenService;
import com.partcraft.back.service.UserService;
import com.partcraft.back.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtils cookieUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService,
                          RefreshTokenService refreshTokenService, CookieUtils cookieUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.cookieUtils = cookieUtils;
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user with email and password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @Parameter(description = "Login credentials", required = true)
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) throws AuthException {
        try {
            var userEntity = userService.getUserEntityByEmail(request.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userEntity.getUsername(),
                            request.getPassword()
                    )
            );
            AuthResponseDTO authResponseDTO = userService.generateTokensForUser(userEntity);

            cookieUtils.setAuthCookies(response, authResponseDTO.getTokens().getAccessToken(), authResponseDTO.getTokens().getRefreshToken());
            return ResponseEntity.ok(authResponseDTO.getUser());
        }catch (NotFoundException | BadCredentialsException e) {
            throw new AuthException("Invalid email or password");
        }
    }

    @Operation(
            summary = "Create a new user account",
            description = "Registers a new user account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> signUp(
            @Parameter(description = "User registration data", required = true)
            @RequestBody CreateUserDTO request,
            HttpServletResponse response) {
        var authResponseDTO = userService.createUser(request);
        cookieUtils.setAuthCookies(response, authResponseDTO.getTokens().getAccessToken(), authResponseDTO.getTokens().getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(authResponseDTO.getUser());
    }


    @Operation(
            summary = "Refresh access token",
            description = "Generates new tokens using the refresh token cookie."
    )
    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh(
            HttpServletResponse response,
            @Parameter(description = "Refresh token from HttpOnly cookie")
            @CookieValue(value = "refreshToken", required = false) String refreshToken) throws AuthException {
        if (refreshToken == null) {
            throw new AuthException("Refresh token required");
        }
        if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
            String username = jwtUtils.getUsernameFromRefreshToken(refreshToken);
            refreshTokenService.deleteRefreshToken(refreshToken);
            String newAccessToken = jwtUtils.generateToken(username);
            String newRefreshToken = refreshTokenService.createRefreshToken(username);
            cookieUtils.setAuthCookies(response, newAccessToken, newRefreshToken);
            return ResponseEntity.ok().build();
        }
        throw new AuthException("Invalid refresh token");
    }

    @Operation(summary = "Check username availability")
    @GetMapping("/username-availability/{username}")
    public ResponseEntity<Boolean> isUsernameAvailable(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.verifyUsernameAvailability(username));
    }

    @Operation(summary = "Check email availability")
    @GetMapping("/email-availability/{email}")
    public ResponseEntity<Boolean> isEmailAvailable(@PathVariable String email) {
        return ResponseEntity.ok().body(userService.verifyEmailAvailability(email));
    }

    @Operation(
            summary = "Logout user",
            description = "Invalidates tokens and clears ALL cookies (including JSESSIONID for OAuth users)."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Refresh token from HttpOnly cookie")
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        // Invalidate Token in DB (if exists)
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                refreshTokenService.deleteRefreshToken(refreshToken);
            } catch (Exception e) {
                // Ignore DB errors during logout, prioritize clearing cookies
            }
        }

        // Clear all auth cookies
        cookieUtils.clearAuthCookies(response);

        return ResponseEntity.ok().build();
    }
}