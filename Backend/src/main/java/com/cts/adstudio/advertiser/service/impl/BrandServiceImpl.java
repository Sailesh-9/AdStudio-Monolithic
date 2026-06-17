package com.cts.adstudio.advertiser.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.adstudio.advertiser.dto.request.BrandRequest;
import com.cts.adstudio.advertiser.dto.response.BrandResponse;
import com.cts.adstudio.advertiser.entity.Brand;
import com.cts.adstudio.advertiser.exception.ResourceNotFoundException;
import com.cts.adstudio.advertiser.repository.BrandRepository;
import com.cts.adstudio.advertiser.service.BrandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService{
    
    // Inject automatically by Spring via @RequiredArgsConstructor
    private final BrandRepository brandRepository;

    // Converts request DTO to entity and saves to database
    @Override
    public BrandResponse createBrand(BrandRequest request) {
        Brand brand = Brand.builder()
            .advertiserId(request.getAdvertiserId())
            .brandName(request.getBrandName())
            .category(request.getCategory())
            .allocatedBudget(request.getAllocatedBudget())
            .build();

        Brand saved = brandRepository.save(brand);

        return mapToResponse(saved);

    }

    // Retrieves all brands and maps them to response DTOs
    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Finds brand by ID or throws exception if not found
    @Override
    public BrandResponse getBrandById(Integer id) {
        
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));

        return mapToResponse(brand);

    }

    // Returns all brands belonging to a specific advertiser
    @Override
    public List<BrandResponse> getAllBrandsByAdvertiserId(Integer advertiserId) {
        
        return brandRepository.findByAdvertiserId(advertiserId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

    }

    // Updates existing brand fields and saves changes
    @Override
    public BrandResponse updateBrand(Integer id, BrandRequest request) {
        
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));

        brand.setBrandName(request.getBrandName());
        brand.setCategory(request.getCategory());
        brand.setAllocatedBudget(request.getAllocatedBudget());

        Brand updated = brandRepository.save(brand);

        return mapToResponse(updated);

    }

    // Deletes branch by ID or throws exception if not found
    @Override
    public void deleteBrand(Integer id) {
        
        if(!brandRepository.existsById(id)) throw new ResourceNotFoundException("Brand not found with ID: " + id);

        brandRepository.deleteById(id);
        
    }

    // Maps entity fields to response DTO
    private BrandResponse mapToResponse(Brand brand) {

        BrandResponse response = new BrandResponse();

        response.setBrandId(brand.getBrandId());
        response.setAdvertiserId(brand.getAdvertiserId());
        response.setBrandName(brand.getBrandName());
        response.setCategory(brand.getCategory());
        response.setAllocatedBudget(brand.getAllocatedBudget());
        response.setSpentToDate(brand.getSpentToDate());
        response.setStatus(brand.getStatus() != null ? brand.getStatus().name() : null);
        response.setCreatedAt(brand.getCreatedAt());
        response.setUpdatedAt(brand.getUpdatedAt());

        return response;

    }

}
