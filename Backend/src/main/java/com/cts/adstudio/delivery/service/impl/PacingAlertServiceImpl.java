package com.cts.adstudio.delivery.service.impl;

import com.cts.adstudio.delivery.deliveryconfig.DeliveryConfig;
import com.cts.adstudio.delivery.deliveryexception.DeliveryNotFoundException;
import com.cts.adstudio.delivery.dto.PacingAlertResponse;
import com.cts.adstudio.delivery.dto.PacingCheckRequest;
import com.cts.adstudio.delivery.entity.DeliveryRecord.DeliveryStatus;
import com.cts.adstudio.delivery.entity.PacingAlert;
import com.cts.adstudio.delivery.entity.PacingAlert.AlertStatus;
import com.cts.adstudio.delivery.entity.PacingAlert.AlertType;
import com.cts.adstudio.delivery.repository.AlertRepository;
import com.cts.adstudio.delivery.repository.DeliveryRepository;
import com.cts.adstudio.delivery.service.PacingAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Default {@link PacingAlertService} implementation. Sources delivered actuals
 * from the module's own accepted records and compares them to the planned
 * targets supplied on the request, so it stays self-contained. Thresholds come
 * from {@link DeliveryConfig}. A new alert is only raised when no alert of the
 * same type is already open for the line item, to avoid duplicates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PacingAlertServiceImpl implements PacingAlertService {

    private static final DeliveryStatus COUNTED = DeliveryStatus.Accepted;

    private final DeliveryRepository deliveryRepository;
    private final AlertRepository alertRepository;
    private final DeliveryConfig config;

    @Override
    @Transactional
    public List<PacingAlertResponse> evaluateLineItem(Long lineItemId, PacingCheckRequest target) {
        if (target.getFlightEnd().isBefore(target.getFlightStart())) {
            throw new IllegalArgumentException("flightEnd must not be before flightStart");
        }

        LocalDate today = LocalDate.now();
        long delivered = deliveryRepository.sumImpressionsByLineItemAndStatus(lineItemId, COUNTED);
        BigDecimal spend = deliveryRepository.sumSpendByLineItemAndStatus(lineItemId, COUNTED);
        if (spend == null) spend = BigDecimal.ZERO;

        int raised = 0;

        // 1. Flight end approaching
        long daysToEnd = ChronoUnit.DAYS.between(today, target.getFlightEnd());
        if (daysToEnd >= 0 && daysToEnd <= config.getFlightEndWarningDays()) {
            if (raiseIfAbsent(lineItemId, AlertType.FlightEndApproaching, null, today)) raised++;
        }

        // 2. Budget exhausted
        if (target.getPlannedBudget() != null
                && spend.compareTo(target.getPlannedBudget()) >= 0
                && target.getPlannedBudget().signum() > 0) {
            if (raiseIfAbsent(lineItemId, AlertType.BudgetExhausted, null, today)) raised++;
        }

        // 3 & 4. Under / over delivery (only once the flight has started)
        Long plannedImpressions = target.getPlannedImpressions();
        if (plannedImpressions != null && plannedImpressions > 0 && !today.isBefore(target.getFlightStart())) {
            long totalDays = Math.max(1, ChronoUnit.DAYS.between(target.getFlightStart(), target.getFlightEnd()));
            long elapsedDays = Math.min(totalDays, ChronoUnit.DAYS.between(target.getFlightStart(), today));
            double progress = (double) elapsedDays / totalDays;
            double expected = plannedImpressions * progress;

            if (expected > 0) {
                double pacing = (delivered * 100.0) / expected;
                BigDecimal pacingPercent = BigDecimal.valueOf(pacing).setScale(2, RoundingMode.HALF_UP);

                if (pacingPercent.compareTo(config.getUnderDeliveryThreshold()) < 0) {
                    if (raiseIfAbsent(lineItemId, AlertType.UnderDelivery, pacingPercent, today)) raised++;
                } else if (pacingPercent.compareTo(config.getOverDeliveryThreshold()) > 0) {
                    if (raiseIfAbsent(lineItemId, AlertType.OverDelivery, pacingPercent, today)) raised++;
                }
            }
        }

        log.info("Pacing evaluated for line item {}: delivered={}, spend={}, {} new alert(s)",
                lineItemId, delivered, spend, raised);

        return alertRepository.findByLineItemId(lineItemId).stream()
                .map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacingAlertResponse> getAlerts(String status) {
        List<PacingAlert> alerts;
        if (status == null || status.isBlank()) {
            alerts = alertRepository.findAll();
        } else {
            alerts = alertRepository.findByStatus(parseAlertStatus(status));
        }
        return alerts.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public PacingAlertResponse updateStatus(Long alertId, String newStatus) {
        PacingAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Pacing alert not found with ID: " + alertId));
        alert.setStatus(parseAlertStatus(newStatus));
        return mapToResponse(alertRepository.save(alert));
    }

    // ---- helpers -------------------------------------------------------------

    private boolean raiseIfAbsent(Long lineItemId, AlertType type, BigDecimal pacingPercent, LocalDate today) {
        if (alertRepository.existsByLineItemIdAndAlertTypeAndStatus(lineItemId, type, AlertStatus.Open)) {
            return false;
        }
        PacingAlert alert = PacingAlert.builder()
                .lineItemId(lineItemId)
                .alertType(type)
                .alertDate(today)
                .pacingPercent(pacingPercent)
                .status(AlertStatus.Open)
                .build();
        alertRepository.save(alert);
        log.info("ALERT raised: {} for line item {} (pacing={})", type, lineItemId, pacingPercent);
        return true;
    }

    private AlertStatus parseAlertStatus(String value) {
        try {
            return AlertStatus.valueOf(value.trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + value + ". Allowed: Open, Actioned, Closed");
        }
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
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
