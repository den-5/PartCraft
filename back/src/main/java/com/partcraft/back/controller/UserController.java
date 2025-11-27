package com.partcraft.back.controller;

import com.partcraft.back.dto.User.UpdateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.exception.AuthException;
import com.partcraft.back.service.UserService;
import com.partcraft.back.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "Endpoints for managing user accounts, profiles, and roles")
@SecurityRequirement(name = "cookieAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get current authenticated user",
            description = "Retrieves the profile information of the currently authenticated user based on the security context."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "User service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/")
    public ResponseEntity<UserDTO> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @Operation(
            summary = "Get user by username",
            description = "Retrieves user profile information by username. Accessible to all authenticated users.\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "User service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve (5-20 alphanumeric characters)", required = true, example = "john_doe")
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @Operation(
            summary = "Update current user's sensitive data",
            description = "Allows the authenticated user to update their own sensitive information (email, password, etc.).\n\n" +
                    "**Email format (if updating):** local-part@domain.tld (TLD must be at least 2 characters)\n\n" +
                    "**Password format (if updating):** Minimum 8 characters, must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data, validation error, or user service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update-sensitive/")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @Parameter(description = "User data to update (email and/or password)", required = true)
            @RequestBody UpdateUserDTO updateUserDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO updatedUser = userService.updateUserSensitiveData(updateUserDTO, currentUsername);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Update user's sensitive data by username (Admin only)",
            description = "Allows administrators to update sensitive information for any user by username.\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only\n\n" +
                    "**Email format (if updating):** local-part@domain.tld (TLD must be at least 2 characters)\n\n" +
                    "**Password format (if updating):** Minimum 8 characters, must contain at least one digit, one lowercase letter, one uppercase letter, and one special character. No whitespace allowed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username, update data, validation error, or authentication error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-sensitive/{username}")
    public ResponseEntity<UserDTO> updateUserByUsername(
            @Parameter(description = "Username of the user to update (5-20 alphanumeric characters)", required = true, example = "john_doe")
            @PathVariable String username,
            @Parameter(description = "User data to update (email and/or password)", required = true)
            @RequestBody UpdateUserDTO updateUserDTO) {
        if (username.isBlank()) {
            throw new AuthException("username is blank");
        }
        UserDTO updatedUser = userService.updateUserSensitiveData(updateUserDTO, username);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Delete current user account",
            description = "Allows the authenticated user to delete their own account. This action is irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user account"),
            @ApiResponse(responseCode = "400", description = "User service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(currentUsername);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete user by username (Admin only)",
            description = "Allows administrators to delete any user account by username. This action is irreversible.\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user account"),
            @ApiResponse(responseCode = "400", description = "Invalid username, authentication error, or user service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUserByUsername(
            @Parameter(description = "Username of the user to delete (5-20 alphanumeric characters)", required = true, example = "john_doe")
            @PathVariable String username) {
        if (username.isBlank()) {
            throw new AuthException("username is blank");
        }
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get current user's role",
            description = "Retrieves the role of the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user role",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRole.class))),
            @ApiResponse(responseCode = "400", description = "User service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/role/")
    public ResponseEntity<UserRole> getCurrentUserRole() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserRole(username));
    }

    @Operation(
            summary = "Get user role by username",
            description = "Retrieves the role of a specific user by their username.\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user role",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRole.class))),
            @ApiResponse(responseCode = "400", description = "User service error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.partcraft.back.util.ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/role/{username}")
    public ResponseEntity<UserRole> getUserRoleByUsername(
            @Parameter(description = "Username of the user (5-20 alphanumeric characters)", required = true, example = "john_doe")
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getUserRole(username));
    }

    @Operation(
            summary = "Update user role (Admin only)",
            description = "Allows administrators to change a user's role (USER, ADMIN, etc.).\n\n" +
                    "**Username format:** 5-20 alphanumeric characters only"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user role",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "User not authorized (requires ADMIN role)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid role or username",
                    content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @Parameter(description = "New role for the user (USER, ADMIN)", required = true, example = "ADMIN")
            @RequestParam String role,
            @Parameter(description = "Username of the user to update (5-20 alphanumeric characters)", required = true, example = "john_doe")
            @RequestParam String username) {
        return ResponseEntity.ok(userService.updateUserRole(username, UserRole.valueOf(role.toUpperCase())));
    }
}
