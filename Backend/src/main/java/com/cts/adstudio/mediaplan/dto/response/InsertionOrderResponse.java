package com.cts.adstudio.mediaplan.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class InsertionOrderResponse {
    private Integer ioId;
    private Integer lineItemId;
    private Integer publisherId;
    private LocalDate orderDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer committedImpressions;
    private BigDecimal orderValue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}