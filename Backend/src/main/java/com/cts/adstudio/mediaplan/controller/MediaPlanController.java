package com.cts.adstudio.mediaplan.controller;

import com.cts.adstudio.mediaplan.dto.request.MediaPlanRequest;
import com.cts.adstudio.mediaplan.dto.request.StatusUpdateRequest;
import com.cts.adstudio.mediaplan.dto.response.MediaPlanResponse;
import com.cts.adstudio.mediaplan.service.MediaPlanService;
import com.cts.adstudio.mediaplan.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cts.adstudio.mediaplan.shared.PagedResponse;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/media-plans")
@RequiredArgsConstructor
public class MediaPlanController {

    private final MediaPlanService mediaPlanService;

    @PostMapping //to create a new media plan
    public ResponseEntity<ApiResponse<MediaPlanResponse>> create(
            @Valid @RequestBody MediaPlanRequest request) {
        MediaPlanResponse response = mediaPlanService.createMediaPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Media plan created successfully", response));
    }

    @GetMapping //to fetch all media plans with pagination 20 data per page
    public ResponseEntity<ApiResponse<PagedResponse<MediaPlanResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Media plans fetched", mediaPlanService.getAllMediaPlans(pageable)));
    }

    @GetMapping("/{id}") //to fetch a media plan by its ID
    public ResponseEntity<ApiResponse<MediaPlanResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success("Media plan fetched", mediaPlanService.getMediaPlanById(id)));
    }

    @PutMapping("/{id}") //to update a media plan by its ID
    public ResponseEntity<ApiResponse<MediaPlanResponse>> update(
            @PathVariable Integer id, @Valid @RequestBody MediaPlanRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Media plan updated", mediaPlanService.updateMediaPlan(id, request)));
    }

    @PutMapping("/{id}/status") //to update the status of a media plan by its ID
    public ResponseEntity<ApiResponse<MediaPlanResponse>> updateStatus(
            @PathVariable Integer id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Status updated",
                        mediaPlanService.updateMediaPlanStatus(id, request.getStatus())));
    }

    @DeleteMapping("/{id}") //to delete a media plan by its ID
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        mediaPlanService.deleteMediaPlan(id);
        return ResponseEntity.ok(ApiResponse.success("Media plan deleted", null));
    }
}