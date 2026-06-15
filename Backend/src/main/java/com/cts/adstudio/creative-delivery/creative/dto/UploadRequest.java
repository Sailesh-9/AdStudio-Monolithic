package com.cts.creative.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import com.cts.creative.entity.CreativeAsset.AssetType;

public record UploadRequest(

    @Schema(example = "300")
    @NotNull
    Long brandId,

    @Schema(example = "AdBanner")
    @NotBlank
    String assetName,

    // ✅ THIS FIXES DROPDOWN
    @Schema(implementation = AssetType.class)
    @NotNull
    AssetType assetType,

    @Schema(example = "300")
    @Min(1)
    Integer width,

    @Schema(example = "250")
    @Min(1)
    Integer height
    
) {}