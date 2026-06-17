package com.cts.adstudio.advertiser.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TargetAudienceRequest {
    
    @NotNull(message = "Brief ID is required")
    private Integer briefId;
    private String ageRange;
    private String gender;
    private String interests;
    private String geography;
    private String deviceType;

}
