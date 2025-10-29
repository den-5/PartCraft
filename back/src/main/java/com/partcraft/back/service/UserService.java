package com.partcraft.back.service;

import com.partcraft.back.dto.*;
import com.partcraft.back.entity.User;
import com.partcraft.back.exception.UserServiceException;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.security.JwtUtils;
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

    public UserDTO updateUserSensitiveData(UpdateUserDTO updateUserDTO, String username) throws UserServiceException {
        if (updateUserDTO.getEmail() != null) {
            return updateUserEmail(updateUserDTO.getEmail(), username);
        } else if (updateUserDTO.getPassword() != null) {
            return updateUserPassword(updateUserDTO.getPassword(), username);
        } else if (updateUserDTO.getUsername() != null) {
            return updateUserUsername(updateUserDTO.getUsername(), username);
        }

        throw new UserServiceException("Provided user data is invalid");
    }

    public UserDTO deleteUser(String username) throws UserServiceException {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) throw new UserServiceException("User with username " + username + " not found");
        var userDTO = new UserDTO(user);

        refreshTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
        return userDTO;
    }

    public UserDTO getUserByUsername(String username) {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user != null) return new UserDTO(user);
        else throw new UserServiceException("User with username " + username + " not found");
    }

    public AuthResponseDTO getUserByEmail(String email) {
        var user = userRepository.findUserByEmail(email).orElse(null);
        if (user == null) throw new UserServiceException("User with email " + email + " not found");

        String refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtUtils.generateToken(user.getUsername());
        return new AuthResponseDTO(new UserDTO(user), new JwtTokensDTO(accessToken, refreshToken));
    }


    public boolean verifyEmailAvailability(String email) {
        return userRepository.findUserByEmail(email).orElse(null) == null;
    }

    public boolean verifyUsernameAvailability(String username) {
        return userRepository.findUserByUsername(username).orElse(null) == null;
    }


    private UserDTO updateUserUsername(String newUsername, String username) {
        if (!VerifyUserDataFormat.verifyUsernameFormat(newUsername)) {
            throw new UserServiceException("Incorrect username format");
        } else if (!verifyUsernameAvailability(newUsername)) {
            throw new UserServiceException("Provided username is already in use");
        }

        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new UserServiceException("User with username " + username + " not found");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
        return new UserDTO(user);
    }

    private UserDTO updateUserEmail(String newEmail, String username) throws UserServiceException {
        if (!VerifyUserDataFormat.verifyEmailFormat(newEmail)) {
            throw new UserServiceException("Incorrect email format");
        } else if (!verifyEmailAvailability(newEmail)) {
            throw new UserServiceException("Provided email address is already in use");
        }

        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new UserServiceException("User with username " + username + " not found");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
        return new UserDTO(user);
    }

    private UserDTO updateUserPassword(String newPassword, String username) throws UserServiceException {
        if (!VerifyUserDataFormat.verifyPasswordFormat(newPassword)) {
            throw new UserServiceException("Incorrect password format");
        }

        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new UserServiceException("User with username " + username + " not found");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new UserDTO(user);
    }
}
