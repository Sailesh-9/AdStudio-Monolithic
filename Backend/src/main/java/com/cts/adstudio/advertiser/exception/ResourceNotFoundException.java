package com.cts.adstudio.advertiser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Thrown when a requested resource is not found in the database
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Accepts a custom message describing what was not found
    public ResourceNotFoundException(String message) {
        super(message);
    }
}