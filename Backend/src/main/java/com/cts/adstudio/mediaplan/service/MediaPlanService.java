package com.cts.adstudio.mediaplan.service;

import com.cts.adstudio.mediaplan.dto.request.MediaPlanRequest;
import com.cts.adstudio.mediaplan.dto.response.MediaPlanResponse;
import com.cts.adstudio.mediaplan.shared.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface MediaPlanService {
    MediaPlanResponse createMediaPlan(MediaPlanRequest request);
    MediaPlanResponse getMediaPlanById(Integer planId);
    PagedResponse<MediaPlanResponse> getAllMediaPlans(Pageable pageable);
    MediaPlanResponse updateMediaPlan(Integer planId, MediaPlanRequest request);
    MediaPlanResponse updateMediaPlanStatus(Integer planId, String status);
    void deleteMediaPlan(Integer planId);
}