package com.cts.adstudio.mediaplan.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PacingAlertResponse {
    private Integer alertId;
    private Integer lineItemId;
    private String alertType;
    private LocalDate alertDate;
    private BigDecimal pacingPercent;
    private String status;
    private LocalDateTime createdAt;
}