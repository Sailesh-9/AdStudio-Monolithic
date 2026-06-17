package com.cts.adstudio.delivery.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload for recording a unit of delivery against a line item. {@code source}
 * and {@code status} are optional strings; when omitted they default to
 * {@code PublisherReport} and {@code Accepted} respectively (the common case of
 * entering an accepted publisher report).
 */
@Data
public class DeliveryRequest {

    @NotNull(message = "Line Item ID is required")
    private Long lineItemId;

    /** Insertion order this delivery fulfils (required for publisher reconciliation). */
    private Long ioId;

    /** Campaign brief this delivery rolls up to (required for client-invoice generation). */
    private Long campaignBriefId;

    @NotNull(message = "Reporting date is required")
    private LocalDate reportingDate;

    @NotNull(message = "Delivered impressions are required")
    @Min(value = 0, message = "Delivered impressions cannot be negative")
    private Long deliveredImpressions;

    @NotNull(message = "Clicks are required")
    @Min(value = 0, message = "Clicks cannot be negative")
    private Long clicks;

    @NotNull(message = "Spend is required")
    @DecimalMin(value = "0.0", message = "Spend cannot be negative")
    private BigDecimal spend;

    /** PublisherReport | InternalEntry. Optional; defaults to PublisherReport. */
    private String source;

    /** Accepted | Disputed | PendingVerification. Optional; defaults to Accepted. */
    private String status;
}
