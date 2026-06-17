package com.cts.adstudio.advertiser.dto.response;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class BrandResponse {
    
    private Integer brandId;
    private Integer advertiserId;
    private String brandName;
    private String category;
    private BigDecimal allocatedBudget;
    private BigDecimal spentToDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
