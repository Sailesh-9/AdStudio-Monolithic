package com.cts.adstudio.mediaplan.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;
}