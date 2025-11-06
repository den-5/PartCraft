package com.partcraft.back.exception;

public class ComponentServiceException extends RuntimeException {
    public ComponentServiceException(String message) {
        super(message);
    }

    public ComponentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

