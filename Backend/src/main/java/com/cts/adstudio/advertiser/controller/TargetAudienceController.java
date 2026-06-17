package com.cts.adstudio.advertiser.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.adstudio.advertiser.dto.request.TargetAudienceRequest;
import com.cts.adstudio.advertiser.dto.response.TargetAudienceResponse;
import com.cts.adstudio.advertiser.service.TargetAudienceService;
import com.cts.adstudio.advertiser.shared.ApiResponse;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/target-audiences")
@RequiredArgsConstructor
public class TargetAudienceController {
 
    // Injected automatically by Spring
    private final TargetAudienceService targetAudienceService;

    // POST /api/target-audience - creates a new target audience record
    @PostMapping
    public ResponseEntity<ApiResponse<TargetAudienceResponse>> createTargetAudience(TargetAudienceRequest request) {

        TargetAudienceResponse response = targetAudienceService.createTargetAudience(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Target audience created successfully", response));

    }

    // GET /api/target-audience - returns all target audience records
    @GetMapping
    public ResponseEntity<ApiResponse<List<TargetAudienceResponse>>> getAllTargetAudiences() {

        List<TargetAudienceResponse> response = targetAudienceService.getAllTargetAudiences();

        return ResponseEntity.ok(ApiResponse.success("Target audiences retrieved successfully", response));

    }

    // GET /api/target-audience/{id} - returns one target audience record by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TargetAudienceResponse>> getTargetAudienceById(@PathVariable Integer id) {

        TargetAudienceResponse response = targetAudienceService.getTargetAudienceById(id);

        return ResponseEntity.ok(ApiResponse.success("Target audience retrieved successfully", response));

    }

    // GET /api/target-audience/brief/{briefId} - returns all target audience for a specific brief
    @GetMapping("/brief/{briefId}")
    public ResponseEntity<ApiResponse<List<TargetAudienceResponse>>> getAllAudiencesByBriefId(Integer briefId) {

        List<TargetAudienceResponse> response = targetAudienceService.getAllAudiencesByBriefId(briefId);

        return ResponseEntity.ok(ApiResponse.success("Target audience retrieved successfully", response));

    }

    // PUT /api/target-audience/{id} - updates an existing target audience record
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TargetAudienceResponse>> updateTargetAudience(@PathVariable Integer id, @Valid @RequestBody TargetAudienceRequest request) {

        TargetAudienceResponse response = targetAudienceService.updateTargetAudience(id, request);

        return ResponseEntity.ok(ApiResponse.success("Target audience updated succesfully", response));

    }

    // DELETE /api/target-audience/{id} - deletes a target audience record
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTargetAudience(Integer id) {

        targetAudienceService.deleteTargetAudience(id);

        return ResponseEntity.ok(ApiResponse.success("Target audience deleted successfully", null));

    }
    
}
