package com.cts.adstudio.delivery.deliveryexception;

/**
 * Thrown when a delivery record or pacing alert cannot be found by id.
 * Translated to HTTP 404 by {@link DeliveryExceptionHandler}.
 */
public class DeliveryNotFoundException extends RuntimeException {
    public DeliveryNotFoundException(String message) {
        super(message);
    }
}
