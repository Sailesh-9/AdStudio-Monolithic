package com.cts.adstudio.advertiser.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrandRequest {
    
    @NotNull(message = "Advertiser ID is required")
    private Integer advertiserId;

    @NotBlank(message = "Brand name is required")
    private String brandName;

    private String category;

    private BigDecimal allocatedBudget;

    private String currency;

}
