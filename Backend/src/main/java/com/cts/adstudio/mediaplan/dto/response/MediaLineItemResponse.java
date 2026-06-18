package com.cts.adstudio.mediaplan.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MediaLineItemResponse {
    private Integer lineItemId;
    private Integer planId;
    private String channel;
    private String publisher;
    private String format;
    private Integer plannedImpressions;
    private BigDecimal plannedBudget;
    private BigDecimal cpm;
    private LocalDate flightStart;
    private LocalDate flightEnd;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}