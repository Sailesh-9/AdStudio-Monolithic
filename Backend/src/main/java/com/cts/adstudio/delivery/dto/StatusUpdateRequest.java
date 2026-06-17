package com.cts.adstudio.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Generic status-change payload, used for both delivery-record status
 * (Accepted | Disputed | PendingVerification) and pacing-alert status
 * (Open | Actioned | Closed).
 */
@Data
public class StatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
