package com.cts.adstudio.delivery.controller;

import com.cts.adstudio.delivery.dto.DeliveryRecordResponse;
import com.cts.adstudio.delivery.dto.DeliveryRequest;
import com.cts.adstudio.delivery.dto.DeliverySummaryResponse;
import com.cts.adstudio.delivery.dto.StatusUpdateRequest;
import com.cts.adstudio.delivery.service.DeliveryService;
import com.cts.adstudio.delivery.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST surface for the Campaign Delivery &amp; Performance module (spec 4.6).
 *
 * <p>Records delivery against media line items, exposes performance rollups, and
 * serves the two delivered-figure endpoints the Finance module calls over HTTP
 * when generating client invoices and reconciling publisher invoices:</p>
 * <ul>
 *   <li>{@code GET /api/delivery/campaigns/{briefId}/delivered-spend}</li>
 *   <li>{@code GET /api/delivery/insertion-orders/{ioId}/delivered-value}</li>
 * </ul>
 * <p>Those two paths and the {@code data} field of the envelope are a fixed
 * contract with {@code DeliveryServiceBudgetCalculation} &mdash; do not rename.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    // RECORD a unit of delivery
    // POST /api/delivery/records
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @PostMapping("/records")
    public ResponseEntity<ApiResponse<DeliveryRecordResponse>> recordDelivery(
            @Valid @RequestBody DeliveryRequest request) {
        DeliveryRecordResponse response = deliveryService.recordDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery recorded successfully", response));
    }

    // GET one delivery record by ID
    // GET /api/delivery/records/{id}
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/records/{id}")
    public ResponseEntity<ApiResponse<DeliveryRecordResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivery record fetched", deliveryService.getById(id)));
    }

    // LIST records by status (Accepted | Disputed | PendingVerification)
    // GET /api/delivery/records?status=Accepted
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<DeliveryRecordResponse>>> getByStatus(
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivery records fetched", deliveryService.getByStatus(status)));
    }

    // CHANGE the status of a delivery record (e.g. accept / dispute)
    // PUT /api/delivery/records/{id}/status
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @PutMapping("/records/{id}/status")
    public ResponseEntity<ApiResponse<DeliveryRecordResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivery status updated", deliveryService.updateStatus(id, request.getStatus())));
    }

    // LIST all delivery records for a line item
    // GET /api/delivery/line-items/{lineItemId}/records
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/line-items/{lineItemId}/records")
    public ResponseEntity<ApiResponse<List<DeliveryRecordResponse>>> getByLineItem(
            @PathVariable Long lineItemId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivery records fetched", deliveryService.getByLineItem(lineItemId)));
    }

    // LIST all delivery records for an insertion order
    // GET /api/delivery/insertion-orders/{ioId}/records
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/insertion-orders/{ioId}/records")
    public ResponseEntity<ApiResponse<List<DeliveryRecordResponse>>> getByInsertionOrder(
            @PathVariable Long ioId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivery records fetched", deliveryService.getByInsertionOrder(ioId)));
    }

    // LIST all delivery records for a campaign brief
    // GET /api/delivery/campaigns/{briefId}/records
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/campaigns/{briefId}/records")
    public ResponseEntity<ApiResponse<List<DeliveryRecordResponse>>> getByCampaign(
            @PathVariable Long briefId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivery records fetched", deliveryService.getByCampaign(briefId)));
    }

    // FINANCE CONTRACT: total accepted delivered spend for a campaign brief
    // GET /api/delivery/campaigns/{briefId}/delivered-spend
    @GetMapping("/campaigns/{briefId}/delivered-spend")
    public ResponseEntity<ApiResponse<BigDecimal>> deliveredSpendForCampaign(
            @PathVariable Long briefId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivered spend computed", deliveryService.deliveredSpendForCampaign(briefId)));
    }

    // FINANCE CONTRACT: accepted delivered value for an insertion order
    // GET /api/delivery/insertion-orders/{ioId}/delivered-value
    @GetMapping("/insertion-orders/{ioId}/delivered-value")
    public ResponseEntity<ApiResponse<BigDecimal>> deliveredValueForInsertionOrder(
            @PathVariable Long ioId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Delivered value computed", deliveryService.deliveredValueForInsertionOrder(ioId)));
    }

    // PERFORMANCE SUMMARY for a line item
    // GET /api/delivery/line-items/{lineItemId}/summary
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/line-items/{lineItemId}/summary")
    public ResponseEntity<ApiResponse<DeliverySummaryResponse>> summaryForLineItem(
            @PathVariable Long lineItemId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Line item delivery summary", deliveryService.summaryForLineItem(lineItemId)));
    }

    // PERFORMANCE SUMMARY for an insertion order
    // GET /api/delivery/insertion-orders/{ioId}/summary
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/insertion-orders/{ioId}/summary")
    public ResponseEntity<ApiResponse<DeliverySummaryResponse>> summaryForInsertionOrder(
            @PathVariable Long ioId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Insertion order delivery summary", deliveryService.summaryForInsertionOrder(ioId)));
    }

    // PERFORMANCE SUMMARY for a campaign brief
    // GET /api/delivery/campaigns/{briefId}/summary
    @PreAuthorize("hasAnyRole('DELIVERY_PUBLISHER','ADMIN')")
    @GetMapping("/campaigns/{briefId}/summary")
    public ResponseEntity<ApiResponse<DeliverySummaryResponse>> summaryForCampaign(
            @PathVariable Long briefId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Campaign delivery summary", deliveryService.summaryForCampaign(briefId)));
    }
}
