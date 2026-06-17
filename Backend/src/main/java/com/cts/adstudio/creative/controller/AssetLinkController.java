package com.cts.adstudio.creative.controller;

import com.cts.adstudio.creative.dto.LinkRequest;
import com.cts.adstudio.creative.service.CreativeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/asset-links")
@RequiredArgsConstructor
public class AssetLinkController {

    private final CreativeService service;

    @PostMapping
    public ResponseEntity<?> link(@Valid @RequestBody LinkRequest req) {

        return ResponseEntity.ok(
                service.link(req.assetId(), req.lineItemId())
        );
    }
}