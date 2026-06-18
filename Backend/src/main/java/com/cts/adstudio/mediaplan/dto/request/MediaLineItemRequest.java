package com.cts.adstudio.mediaplan.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MediaLineItemRequest {

    @NotBlank(message = "Channel is required")
    private String channel;  // validated against Channel enum in service

    @NotBlank(message = "Publisher is required")
    private String publisher;

    private String format;

    @NotNull(message = "Planned impressions are required")
    @Min(value = 1, message = "Planned impressions must be at least 1")
    private Integer plannedImpressions;

    @NotNull(message = "Planned budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Planned budget must be positive")
    private BigDecimal plannedBudget;

    private BigDecimal cpm;

    @NotNull(message = "Flight start date is required")
    private LocalDate flightStart;

    @NotNull(message = "Flight end date is required")
    private LocalDate flightEnd;
}