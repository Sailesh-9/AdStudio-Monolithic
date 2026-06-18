package com.cts.adstudio.mediaplan.controller;

import com.cts.adstudio.mediaplan.dto.request.InsertionOrderRequest;
import com.cts.adstudio.mediaplan.dto.request.StatusUpdateRequest;
import com.cts.adstudio.mediaplan.dto.response.InsertionOrderResponse;
import com.cts.adstudio.mediaplan.service.InsertionOrderService;
import com.cts.adstudio.mediaplan.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/insertion-orders")
@RequiredArgsConstructor
public class InsertionOrderController {

    private final InsertionOrderService ioService;

    // Generate (create) an insertion order
    @PostMapping
    public ResponseEntity<ApiResponse<InsertionOrderResponse>> create(
            @Valid @RequestBody InsertionOrderRequest request) {
        InsertionOrderResponse response = ioService.createInsertionOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Insertion order created and sent", response));
    }

    // List all (optionally filter by publisher: /api/insertion-orders?publisherId=5)
    @GetMapping
    public ResponseEntity<ApiResponse<List<InsertionOrderResponse>>> getAll(
            @RequestParam(required = false) Integer publisherId) {
        List<InsertionOrderResponse> data = (publisherId != null)
                ? ioService.getInsertionOrdersByPublisher(publisherId)
                : ioService.getAllInsertionOrders();
        return ResponseEntity.ok(ApiResponse.success("Insertion orders fetched", data));
    }

    // Get one
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InsertionOrderResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success("Insertion order fetched", ioService.getInsertionOrderById(id)));
    }

    // Publisher confirms/rejects → status change (audited)
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InsertionOrderResponse>> updateStatus(
            @PathVariable Integer id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Status updated", ioService.updateStatus(id, request.getStatus())));
    }
}