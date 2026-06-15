package com.cts.adstudio.iam.exception;

/** Thrown when creating a resource that already exists, e.g. a duplicate email (HTTP 409). */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
