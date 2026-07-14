package com.cts.adstudio.mediaplan.service.impl;

import com.cts.adstudio.mediaplan.dto.response.PacingAlertResponse;
import com.cts.adstudio.mediaplan.entity.MediaLineItem;
import com.cts.adstudio.mediaplan.entity.PacingAlert;
import com.cts.adstudio.mediaplan.exception.ResourceNotFoundException;
import com.cts.adstudio.mediaplan.repository.DeliveryRecordRepository;
import com.cts.adstudio.mediaplan.repository.MediaLineItemRepository;
import com.cts.adstudio.mediaplan.repository.PacingAlertRepository;
import com.cts.adstudio.mediaplan.service.PacingAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service("mediaplanPacingAlertServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class PacingAlertServiceImpl implements PacingAlertService {

    private final MediaLineItemRepository lineItemRepository;
    private final DeliveryRecordRepository deliveryRepository;
    private final PacingAlertRepository pacingAlertRepository;

    // Thresholds from the dev plan
    private static final double UNDER_DELIVERY_THRESHOLD = 80.0; 
    private static final double OVER_DELIVERY_THRESHOLD = 110.0;
    private static final long FLIGHT_END_WARNING_DAYS = 3;

    @Override
    public int runPacingCheck() {
        LocalDate today = LocalDate.now();
        int alertsCreated = 0;

        List<MediaLineItem> lineItems = lineItemRepository.findAll();
        log.info("Pacing check started for {} line items on {}", lineItems.size(), today);

        for (MediaLineItem item : lineItems) {
            // Skip items that are completed or have no flight dates
            if (item.getStatus() == MediaLineItem.LineItemStatus.Completed) continue; // flight completed
            if (item.getFlightStart() == null || item.getFlightEnd() == null) continue; // no flight dates
            if (today.isBefore(item.getFlightStart())) continue; // flight not started yet

            Integer lineItemId = item.getLineItemId();

            // --- Gather delivery data ---
            long delivered = deliveryRepository.sumDeliveredImpressions(lineItemId);
            BigDecimal spend = deliveryRepository.sumSpend(lineItemId);

            // --- 1. FlightEndApproaching ---
            long daysToEnd = ChronoUnit.DAYS.between(today, item.getFlightEnd());
            if (daysToEnd >= 0 && daysToEnd <= FLIGHT_END_WARNING_DAYS) {
                if (createAlertIfNotExists(lineItemId,
                        PacingAlert.AlertType.FlightEndApproaching, null, today)) {
                    alertsCreated++;
                }
            }

            // --- 2. BudgetExhausted ---
            if (item.getPlannedBudget() != null
                    && spend.compareTo(item.getPlannedBudget()) >= 0) {
                if (createAlertIfNotExists(lineItemId,
                        PacingAlert.AlertType.BudgetExhausted, null, today)) {
                    alertsCreated++;
                }
            }

            // --- 3 & 4. Under / Over delivery (needs impressions + flight progress) ---
            if (item.getPlannedImpressions() != null && item.getPlannedImpressions() > 0) {
                long totalFlightDays = Math.max(1,
                        ChronoUnit.DAYS.between(item.getFlightStart(), item.getFlightEnd())); // Ensure at least 1 day
                long elapsedDays = Math.min(totalFlightDays,
                        ChronoUnit.DAYS.between(item.getFlightStart(), today)); // Ensure we don't exceed total flight days
                double flightProgress = (double) elapsedDays / totalFlightDays; //formula: elapsed / total flight days

                double expected = item.getPlannedImpressions() * flightProgress; //formula: planned * flight progress

                if (expected > 0) { // Avoid division by zero
                    double pacing = (delivered * 100.0) / expected;
                    BigDecimal pacingPercent = BigDecimal.valueOf(pacing)
                            .setScale(2, RoundingMode.HALF_UP);

                    if (pacing < UNDER_DELIVERY_THRESHOLD) { // Under delivery
                        if (createAlertIfNotExists(lineItemId,
                                PacingAlert.AlertType.UnderDelivery, pacingPercent, today)) {
                            alertsCreated++;
                        }
                    } else if (pacing > OVER_DELIVERY_THRESHOLD) { // Over delivery
                        if (createAlertIfNotExists(lineItemId,
                                PacingAlert.AlertType.OverDelivery, pacingPercent, today)) {
                            alertsCreated++;
                        }
                    }
                }
            }
        }

        log.info("Pacing check finished. {} new alert(s) created.", alertsCreated);
        return alertsCreated;
    }

    @Override
    public List<PacingAlertResponse> getAlertsByStatus(String status) {
        List<PacingAlert> alerts;
        if (status == null || status.isBlank()) {
            alerts = pacingAlertRepository.findAll();
        } else {
            PacingAlert.AlertStatus s;
            try {
                s = PacingAlert.AlertStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status
                        + ". Allowed: Open, Actioned, Closed");
            }
            alerts = pacingAlertRepository.findByStatus(s);
        }
        return alerts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public PacingAlertResponse updateAlertStatus(Integer alertId, String newStatus) {
        PacingAlert alert = pacingAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pacing Alert not found with ID: " + alertId));

        PacingAlert.AlertStatus status;
        try {
            status = PacingAlert.AlertStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus
                    + ". Allowed: Open, Actioned, Closed");
        }

        alert.setStatus(status);
        return mapToResponse(pacingAlertRepository.save(alert));
    }

    private boolean createAlertIfNotExists(Integer lineItemId, PacingAlert.AlertType type,
                                           BigDecimal pacingPercent, LocalDate today) {
        boolean exists = pacingAlertRepository.existsByLineItemIdAndAlertTypeAndStatus(
                lineItemId, type, PacingAlert.AlertStatus.Open);
        if (exists) {
            return false;
        }
        PacingAlert alert = PacingAlert.builder()
                .lineItemId(lineItemId)
                .alertType(type)
                .alertDate(today)
                .pacingPercent(pacingPercent)
                .status(PacingAlert.AlertStatus.Open)
                .build();
        pacingAlertRepository.save(alert);
        log.info("ALERT created: {} for line item {} (pacing={})", type, lineItemId, pacingPercent);
        return true;
    }

    private PacingAlertResponse mapToResponse(PacingAlert a) {
        return PacingAlertResponse.builder()
                .alertId(a.getAlertId())
                .lineItemId(a.getLineItemId())
                .alertType(a.getAlertType() != null ? a.getAlertType().name() : null)
                .alertDate(a.getAlertDate())
                .pacingPercent(a.getPacingPercent())
                .status(a.getStatus() != null ? a.getStatus().name() : null)
                .createdAt(a.getCreatedAt())
                .build();
    }
}