package com.partcraft.back.controller;

import com.partcraft.back.dto.User.UpdateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.service.UserService;
import com.partcraft.back.util.UserRole;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/")
    public ResponseEntity<UserDTO> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update-sensitive")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUser = userService.updateUserSensitiveData(updateUserDTO,
                SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    public UserDTO updateUserRole(@RequestParam String role, @RequestParam String email) {
        return userService.updateUserRole(email, UserRole.valueOf(role.toUpperCase()));
    }

    @DeleteMapping("/")
    public ResponseEntity<UserDTO> deleteUser() {
        UserDTO deletedUser = userService.deleteUser(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(deletedUser);
    }
}
