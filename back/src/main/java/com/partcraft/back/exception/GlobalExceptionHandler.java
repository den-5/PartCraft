package com.partcraft.back.exception;

import com.partcraft.back.exception.service.*;
import com.partcraft.back.util.ErrorResponse;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Business logic errors - keep user-facing messages for guidance
    @ExceptionHandler(ComponentCompatibilityServiceException.class)
    public ResponseEntity<ErrorResponse> handleCompatibilityError(ComponentCompatibilityServiceException exception) {
        log.warn("Component compatibility issue: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("COMPATIBILITY_ERROR", exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(ValidationException exception) {
        log.warn("Validation error: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", exception.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
        log.warn("Resource not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(PCServiceException.class)
    public ResponseEntity<ErrorResponse> handlePCServiceError(PCServiceException exception) {
        log.warn("PC build error: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("PC_BUILD_ERROR", exception.getMessage()));
    }

    @ExceptionHandler(ComponentServiceException.class)
    public ResponseEntity<ErrorResponse> handleComponentServiceError(ComponentServiceException exception) {
        log.warn("Component error: {}", exception.getMessage());
        HttpStatus status = exception.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(new ErrorResponse("COMPONENT_ERROR", exception.getMessage()));
    }

    // Security-related errors - generic messages only
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthError(AuthException exception) {
        log.warn("Auth error: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("AUTH_ERROR", "Authentication failed"));
    }

    @ExceptionHandler(RefreshTokenServiceException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenError(RefreshTokenServiceException exception) {
        log.warn("Token refresh error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("TOKEN_ERROR", "Session expired, please login again"));
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceError(UserServiceException exception) {
        log.warn("User service error: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("USER_ERROR", "User operation failed"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("ACCESS_DENIED", "Access denied"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("AUTH_FAILED", "Invalid credentials"));
    }

    // Catch-all - never expose internal details
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
