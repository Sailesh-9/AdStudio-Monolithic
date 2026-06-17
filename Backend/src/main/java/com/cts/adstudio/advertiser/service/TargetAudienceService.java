package com.cts.adstudio.advertiser.service;

import java.util.List;

import com.cts.adstudio.advertiser.dto.request.TargetAudienceRequest;
import com.cts.adstudio.advertiser.dto.response.TargetAudienceResponse;

public interface TargetAudienceService {

    // Creates a new target audience record under a campaign brief
    TargetAudienceResponse createTargetAudience(TargetAudienceRequest request);

    // Returns all target audience records in the system
    List<TargetAudienceResponse> getAllTargetAudiences();

    // Returns a single target audience record by its ID
    TargetAudienceResponse getTargetAudienceById(Integer id);

    // Returns all audience records belonging to a specific campaign brief
    List<TargetAudienceResponse> getAllAudiencesByBriefId(Integer briefId);

    // Updates an existing target audience record by its ID
    TargetAudienceResponse updateTargetAudience(Integer id, TargetAudienceRequest request);

    // Deletes a target audience record by its ID
    void deleteTargetAudience(Integer id);
    
}
