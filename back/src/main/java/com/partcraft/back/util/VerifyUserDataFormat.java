package com.partcraft.back.util;

import com.partcraft.back.dto.CreateUserDTO;
import com.partcraft.back.exception.UserServiceException;
import java.util.regex.Pattern;

public class VerifyUserDataFormat {

    public static boolean verifyCreateUserDTO(CreateUserDTO createUserDTO) throws UserServiceException {
        if (createUserDTO == null) {
            throw new UserServiceException("no user data provided");
        }

        return verifyEmailFormat(createUserDTO.getEmail()) && verifyUsernameFormat(createUserDTO.getUsername()) &&
                verifyPasswordFormat(createUserDTO.getPassword());
    }


    public static boolean verifyEmailFormat(String email) throws UserServiceException {
        if (email == null || email.trim().isEmpty()) {
            throw new UserServiceException("email is null or empty");
        }

        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$";
        if (!Pattern.matches(regex, email.trim())) {
            throw new UserServiceException("invalid email format");
        }

        return true;
    }

    public static boolean verifyUsernameFormat(String username) throws UserServiceException {
        if (username == null || username.trim().isEmpty()) {
            throw new UserServiceException("username is null or empty");
        }

        String regex = "^[a-zA-Z0-9_]{3,20}$";
        if (!Pattern.matches(regex, username.trim())) {
            throw new UserServiceException("invalid username format");
        }
        return true;
    }

    public static boolean verifyPasswordFormat(String password) throws UserServiceException {
        if (password == null || password.trim().isEmpty()) {
            throw new UserServiceException("password is null or empty");
        }

        String regex = "^(?=\\S+$)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$";
        if (!Pattern.matches(regex, password.trim())) {
            throw new UserServiceException("invalid password format");
        }

        return true;
    }
}
