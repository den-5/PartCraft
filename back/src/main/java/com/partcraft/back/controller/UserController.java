package com.partcraft.back.controller;

import com.partcraft.back.dto.UpdateUserDTO;
import com.partcraft.back.dto.UserDTO;
import com.partcraft.back.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<UserDTO> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/update-sensitive")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUser = userService.updateUserSensitiveData(updateUserDTO,
                SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/delete")
    public ResponseEntity<UserDTO> deleteUser() {
        UserDTO deletedUser = userService.deleteUser(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(deletedUser);
    }

    @GetMapping("/email-availability/{email}")
    public ResponseEntity<Boolean> verifyEmailAvailability(@PathVariable String email) {
        return ResponseEntity.ok(userService.verifyEmailAvailability(email));
    }

    @GetMapping("/username-availability/{username}")
    public ResponseEntity<Boolean> verifyUsernameAvailability(@PathVariable String username) {
        return ResponseEntity.ok(userService.verifyUsernameAvailability(username));
    }

}
