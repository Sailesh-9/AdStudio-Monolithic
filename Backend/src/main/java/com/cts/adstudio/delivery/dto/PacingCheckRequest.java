package com.cts.adstudio.delivery.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Planned targets for a line item, supplied by the caller (typically the media
 * planner, who owns the plan) when asking Delivery to evaluate pacing. Delivery
 * supplies the <em>actuals</em> from its own accepted records and compares them
 * to these planned figures, so the module stays self-contained and does not
 * reach into the media-plan tables.
 */
@Data
public class PacingCheckRequest {

    @NotNull(message = "Planned impressions are required")
    @Min(value = 0, message = "Planned impressions cannot be negative")
    private Long plannedImpressions;

    @NotNull(message = "Planned budget is required")
    @DecimalMin(value = "0.0", message = "Planned budget cannot be negative")
    private BigDecimal plannedBudget;

    @NotNull(message = "Flight start date is required")
    private LocalDate flightStart;

    @NotNull(message = "Flight end date is required")
    private LocalDate flightEnd;
}
