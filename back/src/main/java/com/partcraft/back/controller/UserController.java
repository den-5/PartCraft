package com.partcraft.back.controller;

import com.partcraft.back.dto.User.UpdateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.exception.AuthException;
import com.partcraft.back.service.UserService;
import com.partcraft.back.enums.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get current authenticated user
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/")
    public ResponseEntity<UserDTO> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // Get user by username
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // Update current authenticated user (for regular users)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update-sensitive/")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UpdateUserDTO updateUserDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO updatedUser = userService.updateUserSensitiveData(updateUserDTO, currentUsername);
        return ResponseEntity.ok(updatedUser);
    }

    // Update user by username (for admins)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-sensitive/{username}")
    public ResponseEntity<UserDTO> updateUserByUsername(@PathVariable String username, @RequestBody UpdateUserDTO updateUserDTO) {
        if (username.isBlank()) {
            throw new AuthException("username is blank");
        }
        UserDTO updatedUser = userService.updateUserSensitiveData(updateUserDTO, username);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete current authenticated user
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(currentUsername);
        return ResponseEntity.ok().build();
    }

    // Delete user by username (for admins)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        if (username.isBlank()) {
            throw new AuthException("username is blank");
        }
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/role/")
    public ResponseEntity<UserRole> getCurrentUserRole() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserRole(username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/role/{username}")
    public ResponseEntity<UserRole> getUserRoleByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserRole(username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    public ResponseEntity<UserDTO> updateUserRole(@RequestParam String role, @RequestParam String username) {
        return ResponseEntity.ok(userService.updateUserRole(username, UserRole.valueOf(role.toUpperCase())));
    }
}
