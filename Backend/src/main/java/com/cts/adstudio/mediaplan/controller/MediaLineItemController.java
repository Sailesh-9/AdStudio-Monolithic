package com.cts.adstudio.mediaplan.controller;

import com.cts.adstudio.mediaplan.dto.request.MediaLineItemRequest;
import com.cts.adstudio.mediaplan.dto.request.StatusUpdateRequest;
import com.cts.adstudio.mediaplan.dto.response.MediaLineItemResponse;
import com.cts.adstudio.mediaplan.service.MediaLineItemService;
import com.cts.adstudio.mediaplan.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MediaLineItemController {

    private final MediaLineItemService lineItemService;

    // CREATE a line item under a plan
    // POST /api/media-plans/{planId}/line-items
    @PostMapping("/api/media-plans/{planId}/line-items")
    public ResponseEntity<ApiResponse<MediaLineItemResponse>> create(
            @PathVariable Integer planId,
            @Valid @RequestBody MediaLineItemRequest request) {
        MediaLineItemResponse response = lineItemService.createLineItem(planId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Line item created successfully", response));
    }

    // LIST all line items under a plan
    // GET /api/media-plans/{planId}/line-items
    @GetMapping("/api/media-plans/{planId}/line-items")
    public ResponseEntity<ApiResponse<List<MediaLineItemResponse>>> getByPlan(
            @PathVariable Integer planId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Line items fetched", lineItemService.getLineItemsByPlan(planId)));
    }

    // GET one line item by its own ID
    // GET /api/line-items/{id}
    @GetMapping("/api/line-items/{id}")
    public ResponseEntity<ApiResponse<MediaLineItemResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Line item fetched", lineItemService.getLineItemById(id)));
    }

    // UPDATE a line item
    // PUT /api/line-items/{id}
    @PutMapping("/api/line-items/{id}")
    public ResponseEntity<ApiResponse<MediaLineItemResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody MediaLineItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Line item updated", lineItemService.updateLineItem(id, request)));
    }

    // CHANGE status
    // PUT /api/line-items/{id}/status
    @PutMapping("/api/line-items/{id}/status")
    public ResponseEntity<ApiResponse<MediaLineItemResponse>> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Status updated", lineItemService.updateLineItemStatus(id, request.getStatus())));
    }

    // DELETE
    // DELETE /api/line-items/{id}
    @DeleteMapping("/api/line-items/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        lineItemService.deleteLineItem(id);
        return ResponseEntity.ok(ApiResponse.success("Line item deleted", null));
    }
}