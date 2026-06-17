package com.cts.adstudio.advertiser.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class CampaignBriefResponse {
    
    private Integer briefId;
    private Integer brandId;
    private String campaignName;
    private String objective;
    private String targetDemographic;
    private String geography;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalBudget;
    private String channelPreference;
    private Integer submittedById;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
