package com.cts.adstudio.delivery.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Read model for a single delivery record. {@code ctr} (click-through rate, %)
 * is derived from clicks and impressions for convenience and is not stored.
 */
@Data
@Builder
public class DeliveryRecordResponse {
    private Long deliveryId;
    private Long lineItemId;
    private Long ioId;
    private Long campaignBriefId;
    private LocalDate reportingDate;
    private Long deliveredImpressions;
    private Long clicks;
    private BigDecimal spend;
    private BigDecimal ctr;
    private String source;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
