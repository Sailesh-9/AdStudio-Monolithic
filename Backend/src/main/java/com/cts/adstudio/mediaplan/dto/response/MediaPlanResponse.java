package com.cts.adstudio.mediaplan.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MediaPlanResponse {
    private Integer planId;
    private Integer briefId;
    private Integer plannerId;
    private BigDecimal totalBudgetAllocated;
    private String channelMix;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}