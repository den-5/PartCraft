package com.partcraft.back.util;

import com.partcraft.back.dto.CreateUserDTO;
import com.partcraft.back.exception.UserServiceException;
import com.partcraft.back.exception.ValidationException;

import java.util.regex.Pattern;

public class VerifyUserDataFormat {

    public static boolean verifyCreateUserDTO(CreateUserDTO createUserDTO) throws ValidationException {
        if (createUserDTO == null) {
            throw new ValidationException("no user data provided");
        }

        return verifyEmailFormat(createUserDTO.getEmail()) && verifyUsernameFormat(createUserDTO.getUsername()) &&
                verifyPasswordFormat(createUserDTO.getPassword());
    }


    public static boolean verifyEmailFormat(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email is null or empty");
        }

        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$";
        if (!Pattern.matches(regex, email.trim())) {
            throw new ValidationException("invalid email format");
        }

        return true;
    }

    public static boolean verifyUsernameFormat(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("username is null or empty");
        }

        String regex = "^[a-zA-Z0-9_]{3,20}$";
        if (!Pattern.matches(regex, username.trim())) {
            throw new ValidationException("invalid username format");
        }
        return true;
    }

    public static boolean verifyPasswordFormat(String password) throws ValidationException {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("password is null or empty");
        }

        String regex = "^(?=\\S+$)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$";
        if (!Pattern.matches(regex, password.trim())) {
            throw new ValidationException("invalid password format");
        }

        return true;
    }
}
