package com.cts.adstudio.delivery.service;

import com.cts.adstudio.delivery.dto.PacingAlertResponse;
import com.cts.adstudio.delivery.dto.PacingCheckRequest;

import java.util.List;

/**
 * Pacing-exception engine for the Delivery module. Compares accepted delivery
 * actuals against caller-supplied planned targets and raises/queries/updates
 * pacing alerts.
 */
public interface PacingAlertService {

    /**
     * Evaluate one line item against the supplied planned targets and raise any
     * applicable alerts (under/over delivery, budget exhausted, flight end
     * approaching). Returns the alerts that exist for the line item after the
     * run (newly raised plus any still-open ones).
     */
    List<PacingAlertResponse> evaluateLineItem(Long lineItemId, PacingCheckRequest target);

    /** List alerts, optionally filtered by status (Open | Actioned | Closed). */
    List<PacingAlertResponse> getAlerts(String status);

    /** Move an alert through its workflow (Open -> Actioned -> Closed). */
    PacingAlertResponse updateStatus(Long alertId, String newStatus);
}
