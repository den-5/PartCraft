package com.partcraft.back.exception;

public class UserServiceNotFoundException extends RuntimeException {
    public UserServiceNotFoundException(String message) {
        super(message);
    }
}
