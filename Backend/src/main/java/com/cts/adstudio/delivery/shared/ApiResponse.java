package com.cts.adstudio.delivery.shared;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Standard response envelope for every Delivery endpoint. Mirrors the envelope
 * used by the other business modules so the whole monolith returns a consistent
 * shape: {@code {success, message, data, timestamp}}.
 *
 * <p>The {@code data} field is important for the cross-module contract: the
 * Finance module's {@code DeliveryServiceBudgetCalculation} reads the numeric
 * {@code data} value from the {@code delivered-spend} / {@code delivered-value}
 * endpoints (it deserializes a minimal {@code {data}} view and ignores the rest),
 * so the field name must stay {@code data}.</p>
 */
@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
