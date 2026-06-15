package com.cts.creative.dto;

import jakarta.validation.constraints.*;

public record ApprovalRequest(
        @NotNull Long assetId,
        @NotBlank String decision
) {}