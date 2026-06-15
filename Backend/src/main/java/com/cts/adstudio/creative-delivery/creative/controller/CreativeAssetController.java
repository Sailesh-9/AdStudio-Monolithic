package com.cts.creative.controller;

import com.cts.creative.entity.CreativeAsset;
import com.cts.creative.service.CreativeService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/creative-assets")
@RequiredArgsConstructor
public class CreativeAssetController {

    private final CreativeService service;

    // ✅ ONLY UPLOAD HERE ✅
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long brandId,
            @RequestParam String assetName,
            @RequestParam CreativeAsset.AssetType assetType,
            @RequestParam Integer width,
            @RequestParam Integer height
    ) throws Exception {

        return ResponseEntity.ok(
                service.uploadManual(file, brandId, assetName, assetType, width, height)
        );
    }

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}