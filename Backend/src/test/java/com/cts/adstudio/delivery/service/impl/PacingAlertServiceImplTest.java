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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PacingAlertServiceImpl}. The two repositories are mocked
 * and a real {@link DeliveryConfig} (default thresholds 80 / 110 / 3 days) is
 * injected, so these exercise the pacing logic itself: each alert type, the
 * flight-progress maths, the de-duplication guard, and the status workflow.
 * Flight windows are built relative to {@link LocalDate#now()} so the elapsed
 * fraction is deterministic regardless of when the suite runs.
 */
@ExtendWith(MockitoExtension.class)
class PacingAlertServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private AlertRepository alertRepository;

    private PacingAlertServiceImpl service;

    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        service = new PacingAlertServiceImpl(deliveryRepository, alertRepository, new DeliveryConfig());
    }

    private PacingCheckRequest target(long plannedImpressions, String plannedBudget,
                                      LocalDate start, LocalDate end) {
        PacingCheckRequest t = new PacingCheckRequest();
        t.setPlannedImpressions(plannedImpressions);
        t.setPlannedBudget(new BigDecimal(plannedBudget));
        t.setFlightStart(start);
        t.setFlightEnd(end);
        return t;
    }

    private PacingAlert alert(Long id, AlertType type, AlertStatus status, String pacing) {
        return PacingAlert.builder()
                .alertId(id)
                .lineItemId(1L)
                .alertType(type)
                .alertDate(today)
                .pacingPercent(pacing == null ? null : new BigDecimal(pacing))
                .status(status)
                .build();
    }

    // ---- evaluateLineItem ----------------------------------------------------

    @Test
    void evaluate_flightEndBeforeStart_throws() {
        PacingCheckRequest t = target(1000L, "100.00", today, today.minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> service.evaluateLineItem(1L, t));
        verifyNoInteractions(deliveryRepository, alertRepository);
    }

    @Test
    void evaluate_underPacing_raisesUnderDeliveryAlert() {
        // mid-flight (50% elapsed), expected = 50_000, delivered = 20_000 -> 40% pacing
        PacingCheckRequest t = target(100_000L, "100000.00",
                today.minusDays(5), today.plusDays(5));
        when(deliveryRepository.sumImpressionsByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(20_000L);
        when(deliveryRepository.sumSpendByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(new BigDecimal("1000.00"));
        when(alertRepository.existsByLineItemIdAndAlertTypeAndStatus(1L, AlertType.UnderDelivery, AlertStatus.Open))
                .thenReturn(false);
        when(alertRepository.findByLineItemId(1L))
                .thenReturn(List.of(alert(1L, AlertType.UnderDelivery, AlertStatus.Open, "40.00")));

        List<PacingAlertResponse> result = service.evaluateLineItem(1L, t);

        ArgumentCaptor<PacingAlert> captor = ArgumentCaptor.forClass(PacingAlert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(AlertType.UnderDelivery, captor.getValue().getAlertType());
        assertEquals(AlertStatus.Open, captor.getValue().getStatus());
        assertEquals(new BigDecimal("40.00"), captor.getValue().getPacingPercent());
        assertEquals(1, result.size());
        assertEquals("UnderDelivery", result.get(0).getAlertType());
    }

    @Test
    void evaluate_overPacing_raisesOverDeliveryAlert() {
        // mid-flight, expected = 50_000, delivered = 80_000 -> 160% pacing
        PacingCheckRequest t = target(100_000L, "100000.00",
                today.minusDays(5), today.plusDays(5));
        when(deliveryRepository.sumImpressionsByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(80_000L);
        when(deliveryRepository.sumSpendByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(new BigDecimal("1000.00"));
        when(alertRepository.existsByLineItemIdAndAlertTypeAndStatus(1L, AlertType.OverDelivery, AlertStatus.Open))
                .thenReturn(false);
        when(alertRepository.findByLineItemId(1L))
                .thenReturn(List.of(alert(1L, AlertType.OverDelivery, AlertStatus.Open, "160.00")));

        List<PacingAlertResponse> result = service.evaluateLineItem(1L, t);

        ArgumentCaptor<PacingAlert> captor = ArgumentCaptor.forClass(PacingAlert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(AlertType.OverDelivery, captor.getValue().getAlertType());
        assertEquals(new BigDecimal("160.00"), captor.getValue().getPacingPercent());
        assertEquals(1, result.size());
    }

    @Test
    void evaluate_flightEndWithinWindow_raisesFlightEndApproachingAlert() {
        // ends in 2 days (warning window = 3); pacing held ~100% so no under/over fires
        PacingCheckRequest t = target(70_000L, "100000.00",
                today.minusDays(5), today.plusDays(2));
        when(deliveryRepository.sumImpressionsByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(50_000L);
        when(deliveryRepository.sumSpendByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(new BigDecimal("1000.00"));
        when(alertRepository.existsByLineItemIdAndAlertTypeAndStatus(1L, AlertType.FlightEndApproaching, AlertStatus.Open))
                .thenReturn(false);
        when(alertRepository.findByLineItemId(1L))
                .thenReturn(List.of(alert(2L, AlertType.FlightEndApproaching, AlertStatus.Open, null)));

        List<PacingAlertResponse> result = service.evaluateLineItem(1L, t);

        ArgumentCaptor<PacingAlert> captor = ArgumentCaptor.forClass(PacingAlert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(AlertType.FlightEndApproaching, captor.getValue().getAlertType());
        assertNull(captor.getValue().getPacingPercent());
        assertEquals(1, result.size());
        assertEquals("FlightEndApproaching", result.get(0).getAlertType());
    }

    @Test
    void evaluate_spendAtOrAboveBudget_raisesBudgetExhaustedAlert() {
        // flight not started yet -> under/over skipped; spend exceeds planned budget
        PacingCheckRequest t = target(100_000L, "1000.00",
                today.plusDays(1), today.plusDays(10));
        when(deliveryRepository.sumImpressionsByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(0L);
        when(deliveryRepository.sumSpendByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(new BigDecimal("1500.00"));
        when(alertRepository.existsByLineItemIdAndAlertTypeAndStatus(1L, AlertType.BudgetExhausted, AlertStatus.Open))
                .thenReturn(false);
        when(alertRepository.findByLineItemId(1L))
                .thenReturn(List.of(alert(3L, AlertType.BudgetExhausted, AlertStatus.Open, null)));

        List<PacingAlertResponse> result = service.evaluateLineItem(1L, t);

        ArgumentCaptor<PacingAlert> captor = ArgumentCaptor.forClass(PacingAlert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(AlertType.BudgetExhausted, captor.getValue().getAlertType());
        assertEquals(1, result.size());
        assertEquals("BudgetExhausted", result.get(0).getAlertType());
    }

    @Test
    void evaluate_openAlertAlreadyExists_doesNotRaiseDuplicate() {
        // would under-deliver, but an Open UnderDelivery alert already exists
        PacingCheckRequest t = target(100_000L, "100000.00",
                today.minusDays(5), today.plusDays(5));
        when(deliveryRepository.sumImpressionsByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(20_000L);
        when(deliveryRepository.sumSpendByLineItemAndStatus(1L, DeliveryStatus.Accepted))
                .thenReturn(new BigDecimal("1000.00"));
        when(alertRepository.existsByLineItemIdAndAlertTypeAndStatus(1L, AlertType.UnderDelivery, AlertStatus.Open))
                .thenReturn(true);
        when(alertRepository.findByLineItemId(1L))
                .thenReturn(List.of(alert(1L, AlertType.UnderDelivery, AlertStatus.Open, "40.00")));

        List<PacingAlertResponse> result = service.evaluateLineItem(1L, t);

        verify(alertRepository, never()).save(any());
        assertEquals(1, result.size());
    }

    // ---- getAlerts -----------------------------------------------------------

    @Test
    void getAlerts_noStatus_returnsAll() {
        when(alertRepository.findAll()).thenReturn(List.of(
                alert(1L, AlertType.UnderDelivery, AlertStatus.Open, "40.00"),
                alert(2L, AlertType.BudgetExhausted, AlertStatus.Closed, null)));

        List<PacingAlertResponse> result = service.getAlerts(null);

        assertEquals(2, result.size());
        verify(alertRepository).findAll();
    }

    @Test
    void getAlerts_withStatus_filtersByStatus() {
        when(alertRepository.findByStatus(AlertStatus.Open)).thenReturn(List.of(
                alert(1L, AlertType.UnderDelivery, AlertStatus.Open, "40.00")));

        List<PacingAlertResponse> result = service.getAlerts("Open");

        assertEquals(1, result.size());
        assertEquals("Open", result.get(0).getStatus());
    }

    @Test
    void getAlerts_invalidStatus_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getAlerts("Sideways"));
        verifyNoInteractions(alertRepository);
    }

    // ---- updateStatus --------------------------------------------------------

    @Test
    void updateStatus_valid_movesAlertThroughWorkflow() {
        PacingAlert existing = alert(10L, AlertType.UnderDelivery, AlertStatus.Open, "40.00");
        when(alertRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(alertRepository.save(any(PacingAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        PacingAlertResponse response = service.updateStatus(10L, "Actioned");

        assertEquals("Actioned", response.getStatus());
        assertEquals(AlertStatus.Actioned, existing.getStatus());
    }

    @Test
    void updateStatus_missing_throwsNotFound() {
        when(alertRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(DeliveryNotFoundException.class, () -> service.updateStatus(404L, "Closed"));
    }

    @Test
    void updateStatus_invalidStatus_throws() {
        when(alertRepository.findById(10L)).thenReturn(Optional.of(
                alert(10L, AlertType.UnderDelivery, AlertStatus.Open, "40.00")));
        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(10L, "Nope"));
        verify(alertRepository, never()).save(any());
    }
}
