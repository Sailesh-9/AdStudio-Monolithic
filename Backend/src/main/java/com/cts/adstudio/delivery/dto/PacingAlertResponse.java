package com.cts.adstudio.delivery.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Read model for a pacing alert. */
@Data
@Builder
public class PacingAlertResponse {
    private Long alertId;
    private Long lineItemId;
    private String alertType;
    private LocalDate alertDate;
    private BigDecimal pacingPercent;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
