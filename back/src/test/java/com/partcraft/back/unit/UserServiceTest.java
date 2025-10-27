package com.partcraft.back.unit;

import com.partcraft.back.dto.*;
import com.partcraft.back.entity.User;
import com.partcraft.back.exception.UserServiceException;
import com.partcraft.back.repository.RefreshTokenRepository;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.RefreshTokenService;
import com.partcraft.back.service.UserService;
import com.partcraft.back.util.VerifyUserDataFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByUsername_shouldReturnUserDTO_whenUserExists(){
        User user  = new User();
        user.setUsername("test228");
        user.setEmail("testuser@example.com");
        when(userRepository.findUserByUsername("test228")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByUsername("test228");
        assertThat(result.getUsername()).isEqualTo("test228");
        assertThat(result.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    void getUserByUsername_shouldThrowUserServiceException_whenUserDoesNotExist(){
        when(userRepository.findUserByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("nonexistent")).isInstanceOf(UserServiceException.class)
                .hasMessageContaining("User with username nonexistent not found");
    }

    @Test
    void getUserByEmail_shouldReturnUserDTO_whenUserExists(){
        User user  = new User();
        user.setUsername("test228");
        user.setEmail("testuser@example.com");
        when(userRepository.findUserByUsername("testuser@example.com")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByUsername("testuser@example.com");
        assertThat(result.getUsername()).isEqualTo("test228");
        assertThat(result.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    void getUserByEmail_shouldThrowUserServiceException_whenUserDoesNotExist(){
        when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com")).isInstanceOf(UserServiceException.class)
                .hasMessageContaining("User with email nonexistent@example.com not found");
    }

    @Test
    void createUser_shouldReturnAuthResponseDTO_whenUserDoesNotExists(){
        var createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("john228");
        createUserDTO.setPassword("1234Password!!");
        createUserDTO.setEmail("testuser@example.com");

        try(MockedStatic<VerifyUserDataFormat> mockedVerify = mockStatic(VerifyUserDataFormat.class)){
            mockedVerify.when(()-> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(true);

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
    void createUser_shouldThrowUserServiceException_whenUserDataInvalid(){
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("john228");
        createUserDTO.setPassword("1234");
        createUserDTO.setEmail("bademail");

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(()-> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(false);

            assertThatThrownBy(() -> userService.createUser(createUserDTO)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("Provided user data is invalid");
        }
    }


    @Test
    void createUser_shouldThrowUserServiceException_whenUserEmailIsAlreadyTaken(){
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("john228");
        createUserDTO.setPassword("1234Password!!");
        createUserDTO.setEmail("testuser@example.com");

        try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(()-> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(true);

            when(userRepository.findUserByEmail("testuser@example.com")).thenReturn(Optional.of(new User()));

            assertThatThrownBy(() -> userService.createUser(createUserDTO)).isInstanceOf(UserServiceException.class).
                    hasMessageContaining("Provided email address is already in use");
        }
    }

    @Test
    void createUser_shouldThrowUserServiceException_whenUserUsernameIsAlreadyTaken(){
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("john228");
        createUserDTO.setPassword("1234Password!!");
        createUserDTO.setEmail("testuser@example.com");

        try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(()-> VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)).thenReturn(true);

            when(userRepository.findUserByUsername("john228")).thenReturn(Optional.of(new User()));

            assertThatThrownBy(() -> userService.createUser(createUserDTO)).isInstanceOf(UserServiceException.class).
                    hasMessageContaining("Provided username is already in use");
        }
    }

    @Test
    void verifyEmailAvailability_shouldReturnTrue_whenUserDoesNotExist(){
        String email = "testuser@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        assertThat(userService.verifyEmailAvailability(email)).isTrue();
    }
    @Test
    void verifyEmailAvailability_shouldReturnFalse_whenUserExist(){
        String email = "testuser@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(new User()));
        assertThat(userService.verifyEmailAvailability(email)).isFalse();
    }
    @Test
    void verifyUsernameAvailability_shouldReturnTrue_whenUserDoesNotExist(){
        String username = "john228";
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
        assertThat(userService.verifyUsernameAvailability(username)).isTrue();
    }
    @Test
    void verifyUsernameAvailability_shouldReturnFalse_whenUserExist(){
        String username = "john228";
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(new User()));
        assertThat(userService.verifyUsernameAvailability(username)).isFalse();
    }

    @Test
    void deleteUser_shouldReturnUserDTO_whenUserExist(){
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));
        assertThat(userService.deleteUser(id)).isNotNull();
    }


    @Test
    void deleteUser_shouldThrowUerServiceException_whenUserDoesNotExist(){
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(id)).isInstanceOf(UserServiceException.class)
                .hasMessageContaining("User with id " + id + " not found");
    }

    @Test
    void updateUser_shouldReturnUserDTO_whenUserExist(){
        Long id = 1L;
        var updateUserDTO = new UpdateUserDTO("new_john228");

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(true);
            when(userRepository.findUserByUsername(updateUserDTO.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findById(id)).thenReturn(Optional.of(new User()));

            UserDTO result = userService.updateUser(updateUserDTO, id);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(updateUserDTO.getUsername());
        }
    }

    @Test
    void updateUser_shouldThrowUserServiceException_whenUserDoesNotExist(){
        Long id = 1L;
        var updateUserDTO = new UpdateUserDTO("new_john228");

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(true);
            when(userRepository.findUserByUsername(updateUserDTO.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(updateUserDTO, id)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with id " + id + " not found");
        }
    }

    @Test
    void updateUser_shouldThrowUserServiceException_whenUsernameFormatIsInvalid(){
        Long id = 1L;
        var updateUserDTO = new UpdateUserDTO("wrongusername");
        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(false);

            assertThatThrownBy(() -> userService.updateUser(updateUserDTO, id)).isInstanceOf(UserServiceException.class).
                    hasMessageContaining("Incorrect username format");
        }
    }

    @Test
    void updateUser_shouldThrowUserServiceException_whenUsernameIsAlreadyInUse(){
        Long id = 1L;
        var updateUserDTO = new UpdateUserDTO("new_john228");

        try (MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(()-> VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())).thenReturn(true);

            when(userRepository.findUserByUsername(updateUserDTO.getUsername())).thenReturn(Optional.of(new User()));

            assertThatThrownBy(() -> userService.updateUser(updateUserDTO, id)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("Provided username is already in use");
        }
    }

    @Test
    void updateUserEmail_shouldReturnUserDTO_whenUserExist(){
        Long id = 1L;
        String email = "newemail@gmail.com";

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyEmailFormat(email)).thenReturn(true);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
            when(userRepository.findById(id)).thenReturn(Optional.of(new User()));

            UserDTO result = userService.updateUserEmail(email, id);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
        }
    }

    @Test
    void updateUserEmail_shouldThrowUserServiceException_whenUserDoesNotExist(){
        Long id = 1L;
        String email = "newemail@gmail.com";

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyEmailFormat(email)).thenReturn(true);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUserEmail(email, id)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with id " + id + " not found");
        }
    }

    @Test
    void updateUserEmail_shouldThrowUserServiceException_whenEmailFormatIsInvalid(){
        Long id = 1L;
        String email = "badnewemail@gmail.com";

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
            mockVerify.when(() -> VerifyUserDataFormat.verifyEmailFormat(email)).thenReturn(false);

            assertThatThrownBy(() -> userService.updateUserEmail(email, id)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("Incorrect email format");
        }
    }

    @Test
    void updateUserEmail_shouldThrowUserServiceException_whenEmailIsAlreadyTaken(){
        Long id = 1L;
        String email = "badnewemail@gmail.com";

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)) {
            mockVerify.when(() -> VerifyUserDataFormat.verifyEmailFormat(email)).thenReturn(true);
            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(new User()));

            assertThatThrownBy(() -> userService.updateUserEmail(email, id)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("Provided email address is already in use");
        }
    }

    @Test
    void updateUserPassword_shouldReturnUserDTO_whenUserExist(){
        Long id = 1L;
        String newPassword = "newPassword123!!";

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyPasswordFormat(newPassword)).thenReturn(true);

            when(userRepository.findById(id)).thenReturn(Optional.of(new User()));

            UserDTO result = userService.updateUserPassword(newPassword, id);

            assertThat(result).isNotNull();
        }
    }

    @Test
    void updateUserPassword_shouldThrowUserServiceException_whenUserDoesNotExist(){
        Long id = 1L;
        String newPassword = "newPassword123!!";

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyPasswordFormat(newPassword)).thenReturn(true);

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUserPassword(newPassword, id)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("User with id " + id + " not found");
        }
    }

    @Test
    void updateUserPassword_shouldThrowUserServiceException_whenPasswordFormatIsInvalid(){
        Long id = 1L;
        String newPassword = "newBadPassword123!!";

        try(MockedStatic<VerifyUserDataFormat> mockVerify = mockStatic(VerifyUserDataFormat.class)){
            mockVerify.when(() -> VerifyUserDataFormat.verifyPasswordFormat(newPassword)).thenReturn(false);

            assertThatThrownBy(() -> userService.updateUserPassword(newPassword, id)).isInstanceOf(UserServiceException.class)
                    .hasMessageContaining("Incorrect password format");
        }
    }

}
