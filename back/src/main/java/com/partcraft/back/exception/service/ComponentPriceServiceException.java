package com.partcraft.back.exception.service;

public class ComponentPriceServiceException extends RuntimeException {
    public ComponentPriceServiceException(String message) {
        super(message);
    }

    public ComponentPriceServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

