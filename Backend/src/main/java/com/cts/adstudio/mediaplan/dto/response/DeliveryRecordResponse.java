package com.cts.adstudio.mediaplan.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryRecordResponse {
    private Integer deliveryId;
    private Integer lineItemId;
    private LocalDate reportingDate;
    private Integer deliveredImpressions;
    private BigDecimal spend;
    private LocalDateTime createdAt;
}