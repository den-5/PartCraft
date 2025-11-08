package com.partcraft.back.exception;

public class PCServiceNotFoundException extends RuntimeException {
    public PCServiceNotFoundException(String message) {
        super(message);
    }
}
