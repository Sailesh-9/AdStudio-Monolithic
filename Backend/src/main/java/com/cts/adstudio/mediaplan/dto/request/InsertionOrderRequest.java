package com.cts.adstudio.mediaplan.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InsertionOrderRequest {

    @NotNull(message = "Line Item ID is required")
    private Integer lineItemId;

    @NotNull(message = "Publisher ID is required")
    private Integer publisherId;

    @NotNull(message = "Order date is required")
    private LocalDate orderDate;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Committed impressions are required")
    @Min(value = 1, message = "Committed impressions must be at least 1")
    private Integer committedImpressions;

    @NotNull(message = "Order value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Order value must be positive")
    private BigDecimal orderValue;
}