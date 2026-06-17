package com.cts.adstudio.advertiser.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.adstudio.advertiser.dto.request.TargetAudienceRequest;
import com.cts.adstudio.advertiser.dto.response.TargetAudienceResponse;
import com.cts.adstudio.advertiser.entity.TargetAudience;
import com.cts.adstudio.advertiser.exception.ResourceNotFoundException;
import com.cts.adstudio.advertiser.repository.TargetAudienceRepository;
import com.cts.adstudio.advertiser.service.TargetAudienceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TargetAudienceServiceImpl implements TargetAudienceService {

    // Injected automatically by Spring via @RequiredArgsConstructor
    private final TargetAudienceRepository targetAudienceRepository;

    // Converts request DTO to entity and saves to database
    @Override
    public TargetAudienceResponse createTargetAudience(TargetAudienceRequest request) {
        
        TargetAudience audience = TargetAudience.builder()
            .briefId(request.getBriefId())
            .ageRange(request.getAgeRange())
            .gender(request.getGender())
            .interests(request.getInterests())
            .geography(request.getGeography())
            .deviceType(request.getDeviceType() != null ? TargetAudience.DeviceType.valueOf(request.getDeviceType()) : null)
            .build();

        TargetAudience saved = targetAudienceRepository.save(audience);

        return mapToResponse(saved);

    }

    // Retrieves all target audience records and maps them to response DTOs
    @Override
    public List<TargetAudienceResponse> getAllTargetAudiences() {
        
        return targetAudienceRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

    }

    // Finds target audience by ID or throws exception if not found
    @Override
    public TargetAudienceResponse getTargetAudienceById(Integer id) {
        
        TargetAudience audience = targetAudienceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Target audience not found with ID: " + id));

        return mapToResponse(audience);

    }

    // Returns all audience records belonging to a specific campaign brief
    @Override
    public List<TargetAudienceResponse> getAllAudiencesByBriefId(Integer briefId) {
        
        return targetAudienceRepository.findByBriefId(briefId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    
    }

    // Update existing target audience fields and saves changes
    @Override
    public TargetAudienceResponse updateTargetAudience(Integer id, TargetAudienceRequest request) {
        
        TargetAudience audience = targetAudienceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Target audience not found with ID: " + id));

        audience.setAgeRange(request.getAgeRange());
        audience.setGender(request.getGender());
        audience.setInterests(request.getInterests());
        audience.setGeography(request.getGeography());
        audience.setDeviceType(request.getDeviceType() != null ? TargetAudience.DeviceType.valueOf(request.getDeviceType()) : null);

        TargetAudience updated = targetAudienceRepository.save(audience);

        return mapToResponse(updated);
        
    }

    // Deltes target audience by ID or throws exception if not found
    @Override
    public void deleteTargetAudience(Integer id) {
        
        if(!targetAudienceRepository.existsById(id)) throw new ResourceNotFoundException("Target audience not found with ID: " + id);

        targetAudienceRepository.deleteById(id);

    }

        // Maps entity fields to response DTO
    private TargetAudienceResponse mapToResponse(TargetAudience audience) {

        TargetAudienceResponse response = new TargetAudienceResponse();

        response.setAudienceId(audience.getAudienceId());
        response.setBriefId(audience.getBriefId());
        response.setAgeRange(audience.getAgeRange());
        response.setGender(audience.getGender());
        response.setInterests(audience.getInterests());
        response.setGeography(audience.getGeography());
        response.setDeviceType(audience.getDeviceType() != null ? audience.getDeviceType().name() : null);
        response.setStatus(audience.getStatus() != null ? audience.getStatus().name() : null);
        response.setCreatedAt(audience.getCreatedAt());
        response.setUpdatedAt(audience.getUpdatedAt());

        return response;

    }
    
}
