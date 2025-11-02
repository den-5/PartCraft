package com.partcraft.back.unit;

import com.partcraft.back.dto.*;
import com.partcraft.back.dto.User.CreateUserDTO;
import com.partcraft.back.dto.User.UpdateUserDTO;
import com.partcraft.back.dto.User.UserDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.exception.UserServiceException;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.RefreshTokenService;
import com.partcraft.back.service.UserService;
import com.partcraft.back.util.VerifyUserDataFormat;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Nested
    class CreateUserTests {
        @Test
        void createUser_shouldReturnAuthResponseDTO_whenUserDoesNotExist() {
            var createUserDTO = createValidUserDTO();
            try (MockedStatic<VerifyUserDataFormat> mockedVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockedVerify.when(() -> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(true);
                when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn("encryptedPassword123!");
                when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
                when(jwtUtils.generateToken("john228")).thenReturn("token123");
                when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn("refreshToken123");
                when(userRepository.findUserByEmail("testuser@example.com")).thenReturn(Optional.empty());
                when(userRepository.findUserByUsername("john228")).thenReturn(Optional.empty());
                AuthResponseDTO response = userService.createUser(createUserDTO);
                assertThat(response).isNotNull();
                assertThat(response.getUser().getUsername()).isEqualTo(createUserDTO.getUsername());
                assertThat(response.getUser().getEmail()).isEqualTo(createUserDTO.getEmail());
                assertThat(response.getTokens().getAccessToken()).isEqualTo("token123");
                assertThat(response.getTokens().getRefreshToken()).isEqualTo("refreshToken123");
            }
        }

        @Test
        void createUser_shouldThrowUserServiceException_whenUserDataInvalid() {
            var createUserDTO = createValidUserDTO();
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(false);
                assertThatThrownBy(() -> userService.createUser(createUserDTO)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Provided user data is invalid");
            }
        }

        @Test
        void createUser_shouldThrowUserServiceException_whenUserEmailIsAlreadyTaken() {
            var createUserDTO = createValidUserDTO();
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(true);
                when(userRepository.findUserByEmail("testuser@example.com")).thenReturn(Optional.of(new User()));
                assertThatThrownBy(() -> userService.createUser(createUserDTO)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Provided email address is already in use");
            }
        }

        @Test
        void createUser_shouldThrowUserServiceException_whenUserUsernameIsAlreadyTaken() {
            var createUserDTO = createValidUserDTO();
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(true);
                when(userRepository.findUserByUsername("john228")).thenReturn(Optional.of(new User()));
                assertThatThrownBy(() -> userService.createUser(createUserDTO)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Provided username is already in use");
            }
        }
    }

    @Nested
    class GetUserTests {
        @Test
        void getUserByUsername_shouldReturnUserDTO_whenUserExists() {
            User user = mockExistingUser();
            when(userRepository.findUserByUsername("john228")).thenReturn(Optional.of(user));
            UserDTO result = userService.getUserByUsername("john228");
            assertThat(result.getUsername()).isEqualTo("john228");
            assertThat(result.getEmail()).isEqualTo("testuser@example.com");
        }

        @Test
        void getUserByUsername_shouldThrowUserServiceException_whenUserDoesNotExist() {
            when(userRepository.findUserByUsername("nonexistent")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.getUserByUsername("nonexistent")).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with username nonexistent not found");
        }

        @Test
        void getUserByEmail_shouldReturnAuthResponseDTO_whenUserExists() {
            User user = mockExistingUser();
            when(userRepository.findUserByEmail("testuser@example.com")).thenReturn(Optional.of(user));
            when(jwtUtils.generateToken("john228")).thenReturn("token123");
            when(refreshTokenService.createRefreshToken(user)).thenReturn("refreshToken123");
            AuthResponseDTO result = userService.getUserByEmail("testuser@example.com");
            assertThat(result.getUser().getUsername()).isEqualTo("john228");
            assertThat(result.getUser().getEmail()).isEqualTo("testuser@example.com");
            assertThat(result.getTokens().getAccessToken()).isEqualTo("token123");
            assertThat(result.getTokens().getRefreshToken()).isEqualTo("refreshToken123");
        }

        @Test
        void getUserByEmail_shouldThrowUserServiceException_whenUserDoesNotExist() {
            when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com")).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with email nonexistent@example.com not found");
        }
    }

    @Nested
    class UpdateUserTests {
        // Username update scenarios
        @Test
        // Success: update username
        void updateUser_shouldReturnUserDTO_whenUserExist() {
            String username = "john228";
            var updateUserDTO = new UpdateUserDTO("new_john228", null, null);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(true);
                when(userRepository.findUserByUsername(updateUserDTO.getUsername())).thenReturn(Optional.empty());
                User user = mockExistingUser();
                when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
                UserDTO result = userService.updateUserSensitiveData(updateUserDTO, username);
                assertThat(result).isNotNull();
                assertThat(result.getUsername()).isEqualTo(updateUserDTO.getUsername());
                assertThat(result.getEmail()).isEqualTo(user.getEmail());
            }
        }

        @Test
            // Error: user not found for username update
        void updateUser_shouldThrowUserServiceException_whenUserDoesNotExist() {
            String username = "john228";
            var updateUserDTO = new UpdateUserDTO("new_john228", null, null);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(true);
                when(userRepository.findUserByUsername(updateUserDTO.getUsername())).thenReturn(Optional.empty());
                when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
                assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTO, username)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("User with username " + username + " not found");
            }
        }

        @Test
            // Error: invalid username format
        void updateUser_shouldThrowUserServiceException_whenUsernameFormatIsInvalid() {
            String username = "john228";
            var updateUserDTO = new UpdateUserDTO("wrongusername", null, null);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(false);
                assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTO, username)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Incorrect username format");
            }
        }

        @Test
            // Error: username already in use
        void updateUser_shouldThrowUserServiceException_whenUsernameIsAlreadyInUse() {
            String username = "john228";
            var updateUserDTO = new UpdateUserDTO("new_john228", null, null);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(true);
                when(userRepository.findUserByUsername(updateUserDTO.getUsername())).thenReturn(Optional.of(new User()));
                assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTO, username)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Provided username is already in use");
            }
        }

        // Email update scenarios
        @Test
        // Success: update email
        void updateUser_shouldReturnUserDTO_whenEmailUpdated() {
            String username = "john228";
            String email = "newemail@gmail.com";
            var updateUserDTO = new UpdateUserDTO(null, email, null);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyEmailFormat(email)).thenReturn(true);
                when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
                User user = mockExistingUser();
                when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
                UserDTO result = userService.updateUserSensitiveData(updateUserDTO, username);
                assertThat(result).isNotNull();
                assertThat(result.getEmail()).isEqualTo(email);
                assertThat(result.getUsername()).isEqualTo(user.getUsername());
            }
        }

        @Test
            // Error: invalid email format
        void updateUser_shouldThrowUserServiceException_whenEmailFormatIsInvalid() {
            String username = "john228";
            String email = "badnewemail@gmail.com";
            var updateUserDTO = new UpdateUserDTO(null, email, null);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyEmailFormat(email)).thenReturn(false);
                assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTO, username)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Incorrect email format");
            }
        }

        @Test
            // Error: email already taken
        void updateUser_shouldThrowUserServiceException_whenEmailIsAlreadyTaken() {
            String username = "john228";
            String email = "badnewemail@gmail.com";
            var updateUserDTO = new UpdateUserDTO(null, email, null);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyEmailFormat(email)).thenReturn(true);
                when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(new User()));
                assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTO, username)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Provided email address is already in use");
            }
        }

        @Test
            // Error: user not found for email update
        void updateUser_shouldThrowUserServiceException_whenUserDoesNotExist_forEmail() {
            String username = "john228";
            var updateUserDTOEmail = new UpdateUserDTO(null, "newemail@gmail.com", null);
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTOEmail, username)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with username " + username + " not found");
        }

        // Password update scenarios
        @Test
        // Success: update password
        void updateUser_shouldReturnUserDTO_whenPasswordUpdated() {
            String username = "john228";
            String newPassword = "newPassword123!!";
            var updateUserDTO = new UpdateUserDTO(null, null, newPassword);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyPasswordFormat(newPassword)).thenReturn(true);
                User user = mockExistingUser();
                when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
                when(passwordEncoder.encode(newPassword)).thenReturn("encryptedPassword");
                UserDTO result = userService.updateUserSensitiveData(updateUserDTO, username);
                assertThat(result).isNotNull();
                assertThat(result.getUsername()).isEqualTo(user.getUsername());
                assertThat(result.getEmail()).isEqualTo(user.getEmail());
            }
        }

        @Test
            // Error: invalid password format
        void updateUser_shouldThrowUserServiceException_whenPasswordFormatIsInvalid() {
            String username = "john228";
            String newPassword = "newBadPassword123!!";
            var updateUserDTO = new UpdateUserDTO(null, null, newPassword);
            try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
                mockVerify.when(() -> VerifyUserDataFormat.verifyPasswordFormat(newPassword)).thenReturn(false);
                assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTO, username)).isInstanceOf(UserServiceException.class)
                        .hasMessageContaining("Incorrect password format");
            }
        }

        @Test
            // Error: user not found for password update
        void updateUser_shouldThrowUserServiceException_whenUserDoesNotExist_forPassword() {
            String username = "john228";
            var updateUserDTOPassword = new UpdateUserDTO(null, null, "newPassword123!!");
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.updateUserSensitiveData(updateUserDTOPassword, username)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with username " + username + " not found");
        }
    }

    @Nested
    class DeleteUserTests {
        @Test
        void deleteUser_shouldReturnUserDTO_whenUserExist() {
            String username = "john228";
            User user = mockExistingUser();
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
            assertThat(userService.deleteUser(username)).isNotNull();
        }

        @Test
        void deleteUser_shouldThrowUserServiceException_whenUserDoesNotExist() {
            String username = "john228";
            when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.deleteUser(username)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with username " + username + " not found");
        }
    }

    private CreateUserDTO createValidUserDTO() {
        var dto = new CreateUserDTO();
        dto.setUsername("john228");
        dto.setPassword("1234Password!!");
        dto.setEmail("testuser@example.com");
        return dto;
    }

    private User mockExistingUser() {
        var user = new User();
        user.setUsername("john228");
        user.setEmail("testuser@example.com");
        return user;
    }
}
