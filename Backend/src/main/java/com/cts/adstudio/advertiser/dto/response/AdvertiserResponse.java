package com.cts.adstudio.advertiser.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdvertiserResponse {
    
    private Integer advertiserId;
    private String companyName;
    private String industry;
    private Integer accountManagerId;
    private BigDecimal annualBudget;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
