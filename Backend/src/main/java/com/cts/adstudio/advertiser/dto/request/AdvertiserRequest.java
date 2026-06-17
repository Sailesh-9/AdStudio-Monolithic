package com.cts.adstudio.advertiser.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdvertiserRequest {
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    private String industry;
    private Integer accountManagerId;
    private BigDecimal annualBudget;
    private String currency;

}
