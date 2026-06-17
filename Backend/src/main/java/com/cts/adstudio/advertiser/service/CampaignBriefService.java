package com.cts.adstudio.advertiser.service;

import java.util.List;

import com.cts.adstudio.advertiser.dto.request.CampaignBriefRequest;
import com.cts.adstudio.advertiser.dto.response.CampaignBriefResponse;

public interface CampaignBriefService {

    // Creates a new campaign brief under a brand
    CampaignBriefResponse createCampaignBrief(CampaignBriefRequest request);

    // Returns all campaign briefs in the system
    List<CampaignBriefResponse> getAllCampaignBriefs();

    // Returns a single campaign brief by its ID
    CampaignBriefResponse getCampaignBriefById(Integer id);

    // Returns all campaign briefs belonging to a specific brand
    List<CampaignBriefResponse> getAllBriefsByBrandId(Integer brandId);

    // Updates an existing campaign brief by its ID
    CampaignBriefResponse updateCampaignBrief(Integer id, CampaignBriefRequest request);

    // Updates only the status of a campaign brief
    CampaignBriefResponse updateCampaignBriefStatus(Integer id, String status);

    // Deletes a campaign brief by its ID
    void deleteCampaignBrief(Integer id);
    
}
