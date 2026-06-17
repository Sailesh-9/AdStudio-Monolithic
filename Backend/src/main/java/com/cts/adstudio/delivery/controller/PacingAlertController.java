package com.cts.adstudio.delivery.controller;

import com.cts.adstudio.delivery.dto.PacingAlertResponse;
import com.cts.adstudio.delivery.dto.PacingCheckRequest;
import com.cts.adstudio.delivery.dto.StatusUpdateRequest;
import com.cts.adstudio.delivery.service.PacingAlertService;
import com.cts.adstudio.delivery.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST surface for pacing exceptions (spec 4.6 &mdash; PacingAlert).
 *
 * <p>The media planner triggers an evaluation by posting the planned targets for
 * a line item; Delivery compares them against its own accepted actuals and raises
 * under/over-delivery, budget-exhausted, and flight-end-approaching alerts. Alerts
 * can then be listed and moved through their workflow (Open &rarr; Actioned &rarr; Closed).</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery/pacing-alerts")
@PreAuthorize("hasAnyRole('MEDIA_PLANNER','ADMIN')")
public class PacingAlertController {

    private final PacingAlertService pacingAlertService;

    // EVALUATE pacing for a line item against supplied planned targets
    // POST /api/delivery/pacing-alerts/line-items/{lineItemId}/evaluate
    @PostMapping("/line-items/{lineItemId}/evaluate")
    public ResponseEntity<ApiResponse<List<PacingAlertResponse>>> evaluate(
            @PathVariable Long lineItemId,
            @Valid @RequestBody PacingCheckRequest request) {
        List<PacingAlertResponse> alerts = pacingAlertService.evaluateLineItem(lineItemId, request);
        return ResponseEntity.ok(ApiResponse.success("Pacing evaluated", alerts));
    }

    // LIST alerts, optionally filtered by status (Open | Actioned | Closed)
    // GET /api/delivery/pacing-alerts?status=Open
    @GetMapping
    public ResponseEntity<ApiResponse<List<PacingAlertResponse>>> getAlerts(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Pacing alerts fetched", pacingAlertService.getAlerts(status)));
    }

    // CHANGE the status of a pacing alert
    // PUT /api/delivery/pacing-alerts/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PacingAlertResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Pacing alert status updated", pacingAlertService.updateStatus(id, request.getStatus())));
    }
}
