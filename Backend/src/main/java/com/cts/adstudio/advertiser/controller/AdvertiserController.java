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

import com.cts.adstudio.advertiser.dto.request.AdvertiserRequest;
import com.cts.adstudio.advertiser.dto.response.AdvertiserResponse;
import com.cts.adstudio.advertiser.service.AdvertiserService;
import com.cts.adstudio.advertiser.shared.ApiResponse;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/advertisers")
@RequiredArgsConstructor
public class AdvertiserController {

    // Injected automatically by Spring
    private final AdvertiserService advertiserService;

    // POST /api/advertisers - creates a new advertiser
    @PostMapping
    public ResponseEntity<ApiResponse<AdvertiserResponse>> createAdvertiser(@Valid @RequestBody AdvertiserRequest request) {

        AdvertiserResponse response = advertiserService.createAdvertiser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Advertiser created successfully", response));

    }

    // GET /api/advertisers - returns all advertisers
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdvertiserResponse>>> getAllAdvertisers() {

        List<AdvertiserResponse> response = advertiserService.getAllAdvertiser();

        return ResponseEntity.ok(ApiResponse.success("Advertisers retrieved successfully", response));

    }

    // GET /api/advertisers/{id} - returns one advertiser by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvertiserResponse>> getAdvertiserById(@PathVariable Integer id) {

        AdvertiserResponse response = advertiserService.getAdvertiserById(id);

        return ResponseEntity.ok(ApiResponse.success("Advertiser retrieved successfully", response));

    }

    // PUT /api/advertisers/{id} - updates an existing advertiser
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvertiserResponse>> updateAdvertiser(@PathVariable Integer id, @Valid @RequestBody AdvertiserRequest request) {

        AdvertiserResponse response = advertiserService.updateAdvertiser(id, request);

        return ResponseEntity.ok(ApiResponse.success("Advertiser updated successfully", response));

    }

    // DELETE /api/advertisers/{id} - deletes an advertiser
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdvertiser(@PathVariable Integer id) {

        advertiserService.deleteAdvertiser(id);

        return ResponseEntity.ok(ApiResponse.success("Advertiser deleted successfully", null));

    }
    
}
