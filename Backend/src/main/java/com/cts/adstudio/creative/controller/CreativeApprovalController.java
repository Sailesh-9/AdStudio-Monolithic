package com.cts.adstudio.creative.controller;

import com.cts.adstudio.creative.dto.ApprovalRequest;
import com.cts.adstudio.creative.service.CreativeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/creative-approvals")
@RequiredArgsConstructor
public class CreativeApprovalController {

    private final CreativeService service;

    // ✅ ONLY APPROVAL API ✅
    @PostMapping
    public ResponseEntity<?> approve(@Valid @RequestBody ApprovalRequest req) {

        return ResponseEntity.ok(
                service.approve(req.assetId(), req.decision())
        );
    }
}