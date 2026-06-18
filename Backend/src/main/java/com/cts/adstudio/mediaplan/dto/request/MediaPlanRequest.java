package com.cts.adstudio.mediaplan.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MediaPlanRequest {

    @NotNull(message = "Brief ID is required")
    private Integer briefId;

    @NotNull(message = "Planner ID is required")
    private Integer plannerId;

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be positive")
    private BigDecimal totalBudgetAllocated;

    private String channelMix;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}