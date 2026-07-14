package com.cts.adstudio.mediaplan.controller;

import com.cts.adstudio.mediaplan.dto.request.DeliveryRecordRequest;
import com.cts.adstudio.mediaplan.dto.response.DeliveryRecordResponse;
import com.cts.adstudio.mediaplan.service.DeliveryRecordService;
import com.cts.adstudio.mediaplan.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeliveryRecordController {

    private final DeliveryRecordService deliveryService;

    // Record delivery data
    @PostMapping("/api/delivery-records")
    public ResponseEntity<ApiResponse<DeliveryRecordResponse>> record(
            @Valid @RequestBody DeliveryRecordRequest request) {
        DeliveryRecordResponse response = deliveryService.recordDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery recorded", response));
    }

    // View all delivery records for a line item
    @GetMapping("/api/line-items/{lineItemId}/delivery")
    public ResponseEntity<ApiResponse<List<DeliveryRecordResponse>>> getByLineItem(
            @PathVariable Integer lineItemId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivery records fetched", deliveryService.getDeliveryByLineItem(lineItemId)));
    }
}