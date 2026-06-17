package com.cts.adstudio.advertiser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cts.adstudio.advertiser.shared.ApiResponse;

@RestControllerAdvice(basePackages = "com.cts.adstudio.advertiser")
public class AdvertiserExceptionHandler {

    // Handles ResourceNotFoundException - returns 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(exception.getMessage()));

    }

    // Handles validation errors from @Valid annotations - returns 400
        @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        // Extracts the first validation error message
        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message));
    }

    // Handles any other unexpected exceptions - returns 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }
    
}
