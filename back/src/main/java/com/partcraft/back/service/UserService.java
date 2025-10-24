package com.partcraft.back.service;

import com.partcraft.back.dto.CreateUserDTO;
import com.partcraft.back.dto.UpdateUserDTO;
import com.partcraft.back.dto.UserDTO;
import com.partcraft.back.entity.User;
import com.partcraft.back.exception.UserServiceException;
import com.partcraft.back.repository.UserRepository;
import com.partcraft.back.util.VerifyUserDataFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder  passwordEncoder;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder  passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(CreateUserDTO createUserDTO) throws UserServiceException {
            if(!VerifyUserDataFormat.verifyCreateUserDTO(createUserDTO)){
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
            return new UserDTO(user);
    }

    public UserDTO updateUser(UpdateUserDTO updateUserDTO, Long id) throws UserServiceException {
        if(!verifyUsernameAvailability(updateUserDTO.getUsername())){
            throw new UserServiceException("Provided username is already in use");
        } else if(!VerifyUserDataFormat.verifyUsernameFormat(updateUserDTO.getUsername())){
            throw new UserServiceException("Incorrect username format");
        }

        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new UserServiceException("User with id " + id + " not found");
        }

        user.setUsername(updateUserDTO.getUsername());
        userRepository.save(user);
        return new UserDTO(user);
    }

    public UserDTO updateUserEmail(String newEmail, Long id) throws UserServiceException {
        if(!verifyEmailAvailability(newEmail)){
            throw new UserServiceException("Provided email address is already in use");
        } else if(!VerifyUserDataFormat.verifyEmailFormat(newEmail)){
            throw new UserServiceException("Incorrect email format");
        }

        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new UserServiceException("User with id " + id + " not found");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
        return new UserDTO(user);
    }

    public UserDTO updateUserPassword(String newPassword, Long id) throws UserServiceException {
        if (!VerifyUserDataFormat.verifyPasswordFormat(newPassword)) {
            throw new UserServiceException("Incorrect password format");
        }

        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new UserServiceException("User with id " + id + " not found");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new UserDTO(user);
    }

    public UserDTO deleteUser(Long id){
        var user = userRepository.findById(id).orElse(null);
        if(user == null) throw new UserServiceException("User with id " + id + " not found");
        var userDTO = new UserDTO(user);

        userRepository.delete(user);
        return userDTO;
    }

    public UserDTO getUserByUsername(String username){
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user != null) return new UserDTO(user);
        else throw new UserServiceException("User with username " + username + " not found");
    }

    public UserDTO getUserByEmail(String email){
        var user = userRepository.findUserByEmail(email).orElse(null);
        if (user != null) return new UserDTO(user);
        else throw new UserServiceException("User with email " + email + " not found");
    }




    public boolean verifyEmailAvailability(String email) {
        return userRepository.findUserByEmail(email).orElse(null) == null;
    }

    public boolean verifyUsernameAvailability(String username) {
        return userRepository.findUserByUsername(username).orElse(null) == null;
    }
}
