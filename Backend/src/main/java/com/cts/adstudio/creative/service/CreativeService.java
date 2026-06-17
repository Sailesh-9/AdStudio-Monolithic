package com.cts.adstudio.creative.service;

import com.cts.adstudio.creative.entity.*;
import com.cts.adstudio.creative.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreativeService {

    private final CreativeAssetRepository assetRepo;
    private final CreativeApprovalRepository approvalRepo;
    private final AssetLineItemLinkRepository linkRepo;

    // ✅ ✅ ✅ UPLOAD
    public CreativeAsset uploadManual(
            MultipartFile file,
            Long brandId,
            String assetName,
            CreativeAsset.AssetType assetType,
            Integer width,
            Integer height
    ) throws Exception {

        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        String dirPath = System.getProperty("user.dir") + "/Backend/creative/uploads/";

        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        String fileName = file.getOriginalFilename().replaceAll("\\s+", "_");
        String filePath = dirPath + fileName;

        file.transferTo(new File(filePath));

        var asset = CreativeAsset.builder()
                .brandId(brandId)
                .assetName(assetName)
                .assetType(assetType)
                .width(width)
                .height(height)
                .filePath(filePath)
                .status(CreativeAsset.Status.Draft)
                .build();

        return assetRepo.save(asset);
    }

    public CreativeApproval approve(Long assetId, String decision) {

    CreativeAsset asset = assetRepo.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));

    if ("Approved".equalsIgnoreCase(decision)) {
        asset.setStatus(CreativeAsset.Status.Approved);
    } else {
        asset.setStatus(CreativeAsset.Status.Rejected);
    }

    // ✅ IMPORTANT — SAVE UPDATED ASSET
    assetRepo.save(asset);

    CreativeApproval approval = CreativeApproval.builder()
            .asset(asset)
            .decision(decision)
            .build();

    return approvalRepo.save(approval);
}

    // ✅ ✅ ✅ LINK (FIXED)
    public AssetLineItemLink link(Long assetId, Long lineItemId) {

        CreativeAsset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        // ✅ check approved
        if (asset.getStatus() != CreativeAsset.Status.Approved) {
            throw new RuntimeException("Asset must be approved before linking");
        }

        // ✅ prevent duplicates
        if (linkRepo.existsByAssetAndLineItemId(asset, lineItemId)) {
            throw new RuntimeException("Duplicate link not allowed");
        }

        AssetLineItemLink link = AssetLineItemLink.builder()
                .asset(asset)
                .lineItemId(lineItemId)
                .build();

        return linkRepo.save(link);
    }

    // ✅ ✅ ✅ GET ALL
    public List<CreativeAsset> getAll() {
        return assetRepo.findAll();
    }
}