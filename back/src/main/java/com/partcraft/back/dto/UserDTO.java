package com.partcraft.back.dto;

import com.partcraft.back.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String email;

    public UserDTO(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
