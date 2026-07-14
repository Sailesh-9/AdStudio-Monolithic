package com.cts.adstudio.mediaplan.controller;

import com.cts.adstudio.mediaplan.dto.request.StatusUpdateRequest;
import com.cts.adstudio.mediaplan.dto.response.PacingAlertResponse;
import com.cts.adstudio.mediaplan.service.PacingAlertService;
import com.cts.adstudio.mediaplan.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController("mediaplanPacingAlertController")
@RequestMapping("/api/pacing-alerts") 
@RequiredArgsConstructor //builder needs it to create the object and then set the values
public class PacingAlertController {

    private final PacingAlertService pacingAlertService;

    // List alerts, optionally filtered: /api/pacing-alerts?status=Open
    @GetMapping
    public ResponseEntity<ApiResponse<List<PacingAlertResponse>>> getAlerts(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Pacing alerts fetched", pacingAlertService.getAlertsByStatus(status)));
    }

    // Action / close an alert
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PacingAlertResponse>> updateStatus(
            @PathVariable Integer id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Alert status updated", pacingAlertService.updateAlertStatus(id, request.getStatus())));
    }

    // MANUAL trigger — so you can test without waiting for the daily 1 AM run
    @PostMapping("/run")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> runNow() {
        int created = pacingAlertService.runPacingCheck();
        return ResponseEntity.ok(ApiResponse.success(
                "Pacing check executed", Map.of("alertsCreated", created)));
    }
}