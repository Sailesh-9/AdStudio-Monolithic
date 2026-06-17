package com.cts.adstudio.advertiser.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.adstudio.advertiser.dto.request.CampaignBriefRequest;
import com.cts.adstudio.advertiser.dto.response.CampaignBriefResponse;
import com.cts.adstudio.advertiser.entity.CampaignBrief;
import com.cts.adstudio.advertiser.exception.ResourceNotFoundException;
import com.cts.adstudio.advertiser.repository.CampaignBriefRepository;
import com.cts.adstudio.advertiser.service.CampaignBriefService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignBriefServiceImpl implements CampaignBriefService {

    // Injected automatically by Spring via @RequiredArgsConstructor
    private final CampaignBriefRepository campaignBriefRepository;

    // Converts request DTO to entity and saves to database
    @Override
    public CampaignBriefResponse createCampaignBrief(CampaignBriefRequest request) {
        
        CampaignBrief brief = CampaignBrief.builder()
            .brandId(request.getBrandId())
            .campaignName(request.getCampaignName())
            .objective(request.getObjective() != null ? CampaignBrief.CampaignObjective.valueOf(request.getObjective()) : null)
            .targetDemographic(request.getTargetDemographic())
            .geography(request.getGeography())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .totalBudget(request.getTotalBudget())
            .channelPreferences(request.getChannelPreferences())
            .submittedById(request.getSubmittedById())
            .build();

        CampaignBrief saved = campaignBriefRepository.save(brief);

        return mapToResponse(saved);
        
    }

    // Retrieves all campaign briefs and maps them to response DTOs
    @Override
    public List<CampaignBriefResponse> getAllCampaignBriefs() {
        return campaignBriefRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // Finds campaign brief by ID or throws exception if not found
    @Override
    public CampaignBriefResponse getCampaignBriefById(Integer id) {

        CampaignBrief brief = campaignBriefRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Campaign brief not found with ID: " + id));

        return mapToResponse(brief);

    }

    // Returns all campaign briefs belonging to a specific brand
    @Override
    public List<CampaignBriefResponse> getAllBriefsByBrandId(Integer brandId) {
        
        return campaignBriefRepository.findByBrandId(brandId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        
    }

    // Updates existing campaign brief fields and saves changes
    @Override
    public CampaignBriefResponse updateCampaignBrief(Integer id, CampaignBriefRequest request) {
        
        CampaignBrief brief = campaignBriefRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign brief not found with ID: " + id));

        brief.setCampaignName(request.getCampaignName());
        brief.setObjective(request.getObjective() != null ? CampaignBrief.CampaignObjective.valueOf(request.getObjective()) : null);
        brief.setTargetDemographic(request.getTargetDemographic());
        brief.setGeography(request.getGeography());
        brief.setStartDate(request.getStartDate());
        brief.setEndDate(request.getEndDate());
        brief.setTotalBudget(request.getTotalBudget());
        brief.setChannelPreferences(request.getChannelPreferences());

        CampaignBrief updated = campaignBriefRepository.save(brief);

        return mapToResponse(updated);

    }

    // Updates only the status of a campaign brief
    @Override
    public CampaignBriefResponse updateCampaignBriefStatus(Integer id, String status) {
        
        CampaignBrief brief = campaignBriefRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign brief not found with ID: " + id));

        brief.setStatus(CampaignBrief.CampaignStatus.valueOf(status));

        CampaignBrief updated = campaignBriefRepository.save(brief);

        return mapToResponse(updated);
        
    }

    // Deletes campaign brief by ID or throws exception if not found
    @Override
    public void deleteCampaignBrief(Integer id) {
        
        if(!campaignBriefRepository.existsById(id)) throw new ResourceNotFoundException("Campaign brief not found with ID: " + id);

        campaignBriefRepository.deleteById(id);

    }

    private CampaignBriefResponse mapToResponse(CampaignBrief brief) {

        CampaignBriefResponse response = new CampaignBriefResponse();

        response.setBriefId(brief.getBriefId());
        response.setBrandId(brief.getBrandId());
        response.setCampaignName(brief.getCampaignName());
        response.setObjective(brief.getObjective() != null ? brief.getObjective().name() : null);
        response.setTargetDemographic(brief.getTargetDemographic());
        response.setGeography(brief.getGeography());
        response.setStartDate(brief.getStartDate());
        response.setEndDate(brief.getEndDate());
        response.setTotalBudget(brief.getTotalBudget());
        response.setChannelPreference(brief.getChannelPreferences());
        response.setSubmittedById(brief.getSubmittedById());
        response.setStatus(brief.getStatus() != null ? brief.getStatus().name() : null);
        response.setCreatedAt(brief.getCreatedAt());
        response.setUpdatedAt(brief.getUpdatedAt());

        return response;

    }
    
}
