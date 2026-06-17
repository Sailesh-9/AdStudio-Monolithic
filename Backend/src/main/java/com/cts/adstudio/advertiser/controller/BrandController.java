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

import com.cts.adstudio.advertiser.dto.request.BrandRequest;
import com.cts.adstudio.advertiser.dto.response.BrandResponse;
import com.cts.adstudio.advertiser.service.BrandService;
import com.cts.adstudio.advertiser.shared.ApiResponse;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    // Injected automatically via Spring
    private final BrandService brandService;

    // POST /api/brands - creates a new brand
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request) {

        BrandResponse response = brandService.createBrand(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Brand created successfully", response));

    }

    // GET /api/brands - returns all brands
    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {

        List<BrandResponse> response = brandService.getAllBrands();

        return ResponseEntity.ok(ApiResponse.success("Brands retrieved successfully", response));

    }

    // GET /api/brands/{id} - returns one brand by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable Integer id) {

        BrandResponse response = brandService.getBrandById(id);

        return ResponseEntity.ok(ApiResponse.success("Brand retrieved successfully", response));

    }

    // GET /api/brands/advertiser/{advertiserId} - returns all brands for a specific advertiser
    @GetMapping("/advertiser/{advertiserId}")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrandsByAdvertiserId(@PathVariable Integer advertiserId) {

        List<BrandResponse> response = brandService.getAllBrandsByAdvertiserId(advertiserId);

        return ResponseEntity.ok(ApiResponse.success("Brand retrieved succesfully", response));

    }

    // PUT /api/brands/{id} - updates an existing brand
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
        @PathVariable Integer id,
        @Valid @RequestBody BrandRequest request) {

            BrandResponse response = brandService.updateBrand(id, request);

            return ResponseEntity.ok(ApiResponse.success("Brand updated successfully", response));

        }

    // DELETE /api/brands/{id} - deletes a brand
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Integer id) {
        
        brandService.deleteBrand(id);

        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", null));

    }
    
}
