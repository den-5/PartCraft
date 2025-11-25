package com.partcraft.back.controller;

import com.partcraft.back.dto.*;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.exception.AuthException;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.RefreshTokenService;
import com.partcraft.back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
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
                .secure(false)       // FIX: Must be false for HTTP (non-SSL) connections
                .path("/")
                .sameSite("Lax")     // FIX: 'Lax' is more compatible than 'Strict' for browser flows
                .maxAge(1800)        // 30 min
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)       // FIX: Must be false for HTTP
                .path("/")
                .sameSite("Lax")     // FIX: 'Lax'
                .maxAge(86400)       // 1 day
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user with email and password. Returns user data and sets HttpOnly cookies for access and refresh tokens.\n\n" +
                    "**Email format:** local-part@domain.tld (TLD must be at least 2 characters)\n\n" +
                    "**Password format:** Minimum 8 characters, must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @Parameter(description = "Login credentials containing email and password", required = true)
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) throws AuthException {

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

    @Operation(
            summary = "Create a new user account",
            description = "Registers a new user account with the provided information. Returns user data and sets HttpOnly cookies for access and refresh tokens.\n\n" +
                    "**Email format:** local-part@domain.tld (TLD must be at least 2 characters)\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only (no special characters or spaces)\n\n" +
                    "**Password format:** Minimum 8 characters, must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data or user already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> signUp(
            @Parameter(description = "User registration data including email, username, and password", required = true)
            @RequestBody CreateUserDTO request,
            HttpServletResponse response) {
        var authResponseDTO = userService.createUser(request);
        setAuthCookies(response, authResponseDTO.getTokens().getAccessToken(), authResponseDTO.getTokens().getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(authResponseDTO.getUser());
    }


    @Operation(
            summary = "Refresh access token",
            description = "Generates new access and refresh tokens using the refresh token from the cookie. The old refresh token is invalidated and new tokens are set as HttpOnly cookies."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens successfully refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token",
                    content = @Content(mediaType = "application/json"))
    })
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
            setAuthCookies(response, newAccessToken, newRefreshToken);
            return ResponseEntity.ok().build();
        }
        throw new AuthException("Invalid refresh token");
    }

    @Operation(
            summary = "Check username availability",
            description = "Checks if the specified username is available for registration. Returns true if the username is available (not taken), false if already in use.\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only (no special characters or spaces)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked username availability. Returns true if available, false if taken.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class, example = "true")))
    })
    @GetMapping("/username-availability/{username}")
    public ResponseEntity<Boolean> isUsernameAvailable(
            @Parameter(description = "Username to check (5-20 alphanumeric characters)", required = true, example = "john_doe")
            @PathVariable String username) {
        return ResponseEntity.ok().body(userService.verifyUsernameAvailability(username));
    }

    @Operation(
            summary = "Check email availability",
            description = "Checks if the specified email is available for registration. Returns true if the email is available (not registered), false if already in use.\n\n" +
                    "**Email format:** local-part@domain.tld (TLD must be at least 2 characters)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked email availability. Returns true if available, false if taken.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class, example = "true")))
    })
    @GetMapping("/email-availability/{email}")
    public ResponseEntity<Boolean> isEmailAvailable(
            @Parameter(description = "Email address to check (format: local-part@domain.tld)", required = true, example = "user@example.com")
            @PathVariable String email) {
        return ResponseEntity.ok().body(userService.verifyEmailAvailability(email));
    }

    @Operation(
            summary = "Logout user (invalidate refresh token)",
            description = "Logs out the user by deleting the provided refresh token from the database or Redis. Only the current session/device is affected."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "400", description = "No refresh token provided or invalid token")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Refresh token from HttpOnly cookie")
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        refreshTokenService.deleteRefreshToken(refreshToken);
        return ResponseEntity.ok().build();
    }
}