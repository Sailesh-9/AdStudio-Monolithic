package com.cts.adstudio.mediaplan.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeliveryRecordRequest {

    @NotNull(message = "Line Item ID is required")
    private Integer lineItemId;

    @NotNull(message = "Reporting date is required")
    private LocalDate reportingDate;

    @NotNull(message = "Delivered impressions are required")
    @Min(value = 0, message = "Delivered impressions cannot be negative")
    private Integer deliveredImpressions;

    @NotNull(message = "Spend is required")
    @DecimalMin(value = "0.0", message = "Spend cannot be negative")
    private BigDecimal spend;
}