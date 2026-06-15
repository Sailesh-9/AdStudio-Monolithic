package com.cts.creative.creativeexception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = "com.cts.creative")
public class CreativeExceptionHandler {

    @ExceptionHandler(CreativeNotFoundException.class)
    public ResponseEntity<String> handleNotFound(CreativeNotFoundException ex) {

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {

        ex.printStackTrace();

        return ResponseEntity.internalServerError()
                .body("Creative Module Error: " + ex.getMessage());
    }
}