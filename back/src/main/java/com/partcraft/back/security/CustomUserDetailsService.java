package com.partcraft.back.security;

import com.partcraft.back.entity.User;
import com.partcraft.back.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Fetch User
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 3. Robust Role Handling
        String roleName = "USER"; // Default fallback
        if (user.getRole() != null) {
            roleName = user.getRole().name(); // Safely get enum name (e.g., "ADMIN", "USER")
        }

        // 4. Handle Password (OAuth2 users often have null passwords)
        String password = user.getPassword();
        if (password == null) {
            password = "";
        }

        // 5. Build UserDetails
        // .roles() automatically adds the "ROLE_" prefix.
        // So .roles("USER") becomes authority "ROLE_USER"
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(password)
                .roles(roleName)
                .build();
    }
}