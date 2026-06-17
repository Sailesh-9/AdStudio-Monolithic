package com.cts.adstudio.advertiser.service;

import java.util.List;

import com.cts.adstudio.advertiser.dto.request.BrandRequest;
import com.cts.adstudio.advertiser.dto.response.BrandResponse;

public interface BrandService {

    // Creates a new brand under an advertiser
    BrandResponse createBrand(BrandRequest request);

    // Returns all brands in the system
    List<BrandResponse> getAllBrands();

    // Returns a single brand by its ID
    BrandResponse getBrandById(Integer id);

    // Returns all brands belonging to a specific advertiser
    List<BrandResponse> getAllBrandsByAdvertiserId(Integer advertiserId);

    // Updates an existing brand by its ID
    BrandResponse updateBrand(Integer id, BrandRequest request);

    // Deletes a brand by its ID
    void deleteBrand(Integer id);
    
}