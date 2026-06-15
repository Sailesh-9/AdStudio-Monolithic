package com.cts.delivery.deliveryexception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = "com.cts.delivery")
public class DeliveryExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception ex) {

        ex.printStackTrace();

        return ResponseEntity.internalServerError()
                .body("Delivery Module Error: " + ex.getMessage());
    }
}