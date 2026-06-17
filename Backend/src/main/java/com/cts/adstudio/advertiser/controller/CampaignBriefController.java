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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.adstudio.advertiser.dto.request.CampaignBriefRequest;
import com.cts.adstudio.advertiser.dto.response.CampaignBriefResponse;
import com.cts.adstudio.advertiser.service.CampaignBriefService;
import com.cts.adstudio.advertiser.shared.ApiResponse;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/campaign-briefs")
@RequiredArgsConstructor
public class CampaignBriefController {

    // Injected automatically by Spring
    private final CampaignBriefService campaignBriefService;

    // POST /api/campaign-briefs - creates a new campaign brief
    @PostMapping
    public ResponseEntity<ApiResponse<CampaignBriefResponse>> createCampaignBrief(@Valid @RequestBody CampaignBriefRequest request) {

        CampaignBriefResponse response = campaignBriefService.createCampaignBrief(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Campaign brief created successfully", response));

    }

    // GET /api/campaign-briefs - returns all campaign briefs
    @GetMapping
    public ResponseEntity<ApiResponse<List<CampaignBriefResponse>>> getAllCampaignBriefs() {

        List<CampaignBriefResponse> response = campaignBriefService.getAllCampaignBriefs();

        return ResponseEntity.ok(ApiResponse.success("Campaign briefs retrieved successfully", response));

    }

    // GET /api/campaign-brief/{id} - returns one campaign brief by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignBriefResponse>> getCampaignBriefById(@PathVariable Integer id) {

        CampaignBriefResponse response = campaignBriefService.getCampaignBriefById(id);

        return ResponseEntity.ok(ApiResponse.success("Campaign brief retrieved successfully", response));

    }

    // GET /api/campaign-briefs/brand/{brandId} - returns all campaign briefs for a specific brand
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<List<CampaignBriefResponse>>> getAllBriefsByBrandId(@PathVariable Integer brandId) {

        List<CampaignBriefResponse> response = campaignBriefService.getAllBriefsByBrandId(brandId);

        return ResponseEntity.ok(ApiResponse.success("Campaign briefs retrieved successfully", response));

    }

    // PUT /api/campaign-briefs/{id} - updates an existing campaign brief
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignBriefResponse>> updateCampaignBrief(@PathVariable Integer id, @Valid @RequestBody CampaignBriefRequest request) {

        CampaignBriefResponse response = campaignBriefService.updateCampaignBrief(id, request);

        return ResponseEntity.ok(ApiResponse.success("Campaign brief updated successfully", response));

    }

    // PUT /api/campaign-briefs/{id}/status - updates only the status of a campaign brief
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CampaignBriefResponse>> updateCampaignBriefStatus(@PathVariable Integer id, @RequestParam String status) {

        CampaignBriefResponse response = campaignBriefService.updateCampaignBriefStatus(id, status);

        return ResponseEntity.ok(ApiResponse.success("Campaign brief status updated successfully", response));

    }

    // DELETE /api/campaign-brief/{id} - deletes a campaign brief
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCampaignBrief(@PathVariable Integer id) {

        campaignBriefService.deleteCampaignBrief(id);

        return ResponseEntity.ok(ApiResponse.success("Campaign brief deleted successfully", null));

    }
    
}
