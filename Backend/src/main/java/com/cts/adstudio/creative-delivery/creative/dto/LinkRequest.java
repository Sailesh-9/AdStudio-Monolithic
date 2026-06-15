package com.cts.creative.dto;

import jakarta.validation.constraints.*;

public record LinkRequest(
        @NotNull Long assetId,
        @NotNull Long lineItemId
) {}