package com.cts.adstudio.advertiser.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TargetAudienceResponse {
    
    private Integer audienceId;
    private Integer briefId;
    private String ageRange;
    private String gender;
    private String interests;
    private String geography;
    private String deviceType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
