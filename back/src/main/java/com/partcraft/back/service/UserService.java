package com.partcraft.back.service;

import com.partcraft.back.dto.*;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.dto.User.UpdateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.exception.service.UserServiceException;
import com.partcraft.back.exception.NotFoundException;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.enums.UserRole;
import com.partcraft.back.util.VerifyUserDataFormat;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuthResponseDTO createUser(CreateUserDTO createUserDTO) throws UserServiceException {
        if (!VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)) {
            throw new UserServiceException("Provided user data is invalid");
        }
        if (!verifyEmailAvailability(createUserDTO.getEmail())) {
            throw new UserServiceException("Provided email address is already in use");
        }
        if (!verifyUsernameAvailability(createUserDTO.getUsername())) {
            throw new UserServiceException("Provided username is already in use");
        }

        var user = new User(createUserDTO.getUsername(),
                createUserDTO.getEmail(),
                passwordEncoder.encode(createUserDTO.getPassword()));
        userRepository.save(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtUtils.generateToken(user.getUsername());
        return new AuthResponseDTO(new UserDTO(user), new JwtTokensDTO(accessToken, refreshToken));
    }


    public void deleteUser(String username) throws UserServiceException {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) throw new NotFoundException("User with username " + username + " not found");

        refreshTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }

    public UserDTO getUserByUsername(String username) {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user != null) return new UserDTO(user);
        else throw new NotFoundException("User with username " + username + " not found");
    }

    public AuthResponseDTO getUserByEmail(String email) {
        if (!VerifyUserDataFormat.verifyEmailFormat(email)) {
            throw new UserServiceException("Invalid email format");
        }
        var user = userRepository.findUserByEmail(email).orElse(null);
        if (user == null) throw new NotFoundException("User with email " + email + " not found");

        String refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtUtils.generateToken(user.getUsername());
        return new AuthResponseDTO(new UserDTO(user), new JwtTokensDTO(accessToken, refreshToken));
    }

    public UserDTO updateUserRole(String username, UserRole role) {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) throw new NotFoundException("User with username " + username + " not found");

        user.setRole(role);
        userRepository.save(user);
        return new UserDTO(user);
    }

    public boolean verifyEmailAvailability(String email) {
        return userRepository.findUserByEmail(email).orElse(null) == null;
    }

    public boolean verifyUsernameAvailability(String username) {
        return userRepository.findUserByUsername(username).orElse(null) == null;
    }

    public UserRole getUserRole(String username) {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) throw new NotFoundException("User with username " + username + " not found");
        return user.getRole();
    }

    public UserDTO updateUserSensitiveData(UpdateUserDTO updateUserDTO, String username) throws UserServiceException {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) throw new NotFoundException("User with username " + username + " not found");

        boolean updated = false;

        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().isBlank()) {
            if (!VerifyUserDataFormat.verifyEmailFormat(updateUserDTO.getEmail())) {
                throw new UserServiceException("Incorrect email format");
            } else if (!verifyEmailAvailability(updateUserDTO.getEmail())) {
                throw new UserServiceException("Provided email address is already in use");
            }
            user.setEmail(updateUserDTO.getEmail());
            updated = true;
        }
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().isBlank()) {
            if (!VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())) {
                throw new UserServiceException("Incorrect username format");
            } else if (!verifyUsernameAvailability(updateUserDTO.getUsername())) {
                throw new UserServiceException("Provided username is already in use");
            }
            user.setUsername(updateUserDTO.getUsername());
            updated = true;
        }
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isBlank()) {
            if (!VerifyUserDataFormat.verifyPasswordFormat(updateUserDTO.getPassword())) {
                throw new UserServiceException("Incorrect password format");
            }
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
            updated = true;
        }

        if (!updated) {
            throw new UserServiceException("Provided user data is invalid");
        }

        userRepository.save(user);
        return new UserDTO(user);
    }
}
