package com.partcraft.back.exception;

import com.partcraft.back.util.ErrorResponse;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceError(UserServiceException exception) {
        log.error("Error in UserService class: ", exception);
        ErrorResponse errorResponse = new ErrorResponse("USER_SERVICE_ERROR", exception.getMessage());
        if (exception.getMessage().endsWith("not found")) {
            return ResponseEntity.status(401).body(errorResponse);
        } else {
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthError(AuthException exception) {
        log.error("Error in AuthController class: ", exception);
        ErrorResponse errorResponse = new ErrorResponse("AUTH_CONTROLLER_ERROR", exception.getMessage());
        return ResponseEntity.status(401).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(ValidationException exception) {
        log.error("Error in ValidateUserData class: ", exception);
        ErrorResponse errorResponse = new ErrorResponse("VALIDATE_USER_DATA_ERROR", exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RefreshTokenServiceException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenError(RefreshTokenServiceException exception) {
        log.error("Error in RefreshTokenService class: ", exception);
        ErrorResponse errorResponse = new ErrorResponse("REFRESH_TOKEN_SERVICE_ERROR", exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(PCServiceException.class)
    public ResponseEntity<ErrorResponse> handlePCServiceError(PCServiceException exception) {
        log.error("Error in PCService class: ", exception);
        ErrorResponse errorResponse = new ErrorResponse("PC_SERVICE_ERROR", exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Unexpected error: " + ex.getMessage()));
    }

}
