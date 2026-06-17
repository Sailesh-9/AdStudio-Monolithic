package com.cts.adstudio.delivery.service.impl;

import com.cts.adstudio.delivery.dto.DeliveryRecordResponse;
import com.cts.adstudio.delivery.dto.DeliveryRequest;
import com.cts.adstudio.delivery.dto.DeliverySummaryResponse;
import com.cts.adstudio.delivery.entity.DeliveryRecord;
import com.cts.adstudio.delivery.entity.DeliveryRecord.DeliveryStatus;
import com.cts.adstudio.delivery.entity.DeliveryRecord.Source;
import com.cts.adstudio.delivery.deliveryexception.DeliveryNotFoundException;
import com.cts.adstudio.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DeliveryServiceImpl}. Pure Mockito (no Spring context):
 * the repository is mocked, so these exercise the service's own logic &mdash;
 * accepted-only aggregation, CTR derivation, status validation, the two
 * finance-facing rollups, and the summary reducers.
 */
@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryServiceImpl service;

    private DeliveryRecord record(Long id, Long lineItem, Long io, Long brief,
                                  long impressions, long clicks, String spend,
                                  Source source, DeliveryStatus status) {
        return DeliveryRecord.builder()
                .deliveryId(id)
                .lineItemId(lineItem)
                .ioId(io)
                .campaignBriefId(brief)
                .reportingDate(LocalDate.of(2025, 1, 15))
                .deliveredImpressions(impressions)
                .clicks(clicks)
                .spend(new BigDecimal(spend))
                .source(source)
                .status(status)
                .build();
    }

    // ---- recordDelivery ------------------------------------------------------

    @Test
    void recordDelivery_persistsAndReturnsMappedResponseWithCtr() {
        DeliveryRequest request = new DeliveryRequest();
        request.setLineItemId(10L);
        request.setIoId(20L);
        request.setCampaignBriefId(30L);
        request.setReportingDate(LocalDate.of(2025, 1, 15));
        request.setDeliveredImpressions(1000L);
        request.setClicks(50L);
        request.setSpend(new BigDecimal("123.45"));
        request.setSource("PublisherReport");
        request.setStatus("Accepted");

        when(deliveryRepository.save(any(DeliveryRecord.class))).thenAnswer(inv -> {
            DeliveryRecord r = inv.getArgument(0);
            r.setDeliveryId(1L);
            return r;
        });

        DeliveryRecordResponse response = service.recordDelivery(request);

        assertEquals(1L, response.getDeliveryId());
        assertEquals(10L, response.getLineItemId());
        assertEquals(20L, response.getIoId());
        assertEquals(30L, response.getCampaignBriefId());
        assertEquals(1000L, response.getDeliveredImpressions());
        assertEquals(50L, response.getClicks());
        assertEquals(new BigDecimal("123.45"), response.getSpend());
        // 50 / 1000 * 100 = 5.00
        assertEquals(new BigDecimal("5.00"), response.getCtr());
        assertEquals("PublisherReport", response.getSource());
        assertEquals("Accepted", response.getStatus());
        verify(deliveryRepository).save(any(DeliveryRecord.class));
    }

    @Test
    void recordDelivery_invalidSource_throwsIllegalArgument() {
        DeliveryRequest request = new DeliveryRequest();
        request.setLineItemId(10L);
        request.setReportingDate(LocalDate.of(2025, 1, 15));
        request.setDeliveredImpressions(100L);
        request.setClicks(1L);
        request.setSpend(BigDecimal.TEN);
        request.setSource("Telepathy");

        assertThrows(IllegalArgumentException.class, () -> service.recordDelivery(request));
        verify(deliveryRepository, never()).save(any());
    }

    // ---- getById -------------------------------------------------------------

    @Test
    void getById_found_returnsMappedRecord() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(
                record(1L, 10L, 20L, 30L, 2000L, 40L, "10.00",
                        Source.PublisherReport, DeliveryStatus.Accepted)));

        DeliveryRecordResponse response = service.getById(1L);

        assertEquals(1L, response.getDeliveryId());
        assertEquals(2000L, response.getDeliveredImpressions());
        // 40 / 2000 * 100 = 2.00
        assertEquals(new BigDecimal("2.00"), response.getCtr());
    }

    @Test
    void getById_missing_throwsNotFound() {
        when(deliveryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(DeliveryNotFoundException.class, () -> service.getById(99L));
    }

    // ---- updateStatus --------------------------------------------------------

    @Test
    void updateStatus_valid_savesNewStatus() {
        DeliveryRecord existing = record(1L, 10L, 20L, 30L, 1000L, 10L, "10.00",
                Source.PublisherReport, DeliveryStatus.Accepted);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(deliveryRepository.save(any(DeliveryRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        DeliveryRecordResponse response = service.updateStatus(1L, "Disputed");

        assertEquals("Disputed", response.getStatus());
        assertEquals(DeliveryStatus.Disputed, existing.getStatus());
    }

    @Test
    void updateStatus_invalidStatus_throwsIllegalArgument() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(
                record(1L, 10L, 20L, 30L, 1L, 0L, "0.00",
                        Source.PublisherReport, DeliveryStatus.Accepted)));

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(1L, "Bogus"));
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void updateStatus_missing_throwsNotFound() {
        when(deliveryRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(DeliveryNotFoundException.class, () -> service.updateStatus(5L, "Accepted"));
    }

    // ---- list reads ----------------------------------------------------------

    @Test
    void getByStatus_valid_returnsMappedList() {
        when(deliveryRepository.findByStatus(DeliveryStatus.Accepted)).thenReturn(List.of(
                record(1L, 10L, 20L, 30L, 1000L, 10L, "10.00",
                        Source.PublisherReport, DeliveryStatus.Accepted)));

        List<DeliveryRecordResponse> result = service.getByStatus("Accepted");

        assertEquals(1, result.size());
        assertEquals("Accepted", result.get(0).getStatus());
    }

    @Test
    void getByStatus_invalid_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> service.getByStatus("NotAStatus"));
        verifyNoInteractions(deliveryRepository);
    }

    @Test
    void getByLineItem_returnsMappedList() {
        when(deliveryRepository.findByLineItemId(10L)).thenReturn(List.of(
                record(1L, 10L, 20L, 30L, 1000L, 10L, "10.00",
                        Source.PublisherReport, DeliveryStatus.Accepted),
                record(2L, 10L, 21L, 30L, 2000L, 20L, "20.00",
                        Source.InternalEntry, DeliveryStatus.PendingVerification)));

        List<DeliveryRecordResponse> result = service.getByLineItem(10L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getLineItemId());
    }

    // ---- finance contract ----------------------------------------------------

    @Test
    void deliveredSpendForCampaign_sumsOnlyAcceptedSpend() {
        when(deliveryRepository.sumSpendByCampaignAndStatus(30L, DeliveryStatus.Accepted))
                .thenReturn(new BigDecimal("500.00"));

        BigDecimal result = service.deliveredSpendForCampaign(30L);

        assertEquals(new BigDecimal("500.00"), result);
        verify(deliveryRepository).sumSpendByCampaignAndStatus(30L, DeliveryStatus.Accepted);
    }

    @Test
    void deliveredValueForInsertionOrder_sumsOnlyAcceptedSpend() {
        when(deliveryRepository.sumSpendByIoAndStatus(20L, DeliveryStatus.Accepted))
                .thenReturn(new BigDecimal("250.00"));

        BigDecimal result = service.deliveredValueForInsertionOrder(20L);

        assertEquals(new BigDecimal("250.00"), result);
        verify(deliveryRepository).sumSpendByIoAndStatus(20L, DeliveryStatus.Accepted);
    }

    // ---- summaries -----------------------------------------------------------

    @Test
    void summaryForLineItem_aggregatesAndComputesCtr() {
        when(deliveryRepository.sumImpressionsByLineItemAndStatus(10L, DeliveryStatus.Accepted)).thenReturn(10000L);
        when(deliveryRepository.sumClicksByLineItemAndStatus(10L, DeliveryStatus.Accepted)).thenReturn(200L);
        when(deliveryRepository.sumSpendByLineItemAndStatus(10L, DeliveryStatus.Accepted)).thenReturn(new BigDecimal("999.99"));
        when(deliveryRepository.countByLineItemAndStatus(10L, DeliveryStatus.Accepted)).thenReturn(3L);

        DeliverySummaryResponse summary = service.summaryForLineItem(10L);

        assertEquals("LineItem", summary.getScope());
        assertEquals(10L, summary.getScopeId());
        assertEquals(3L, summary.getRecordCount());
        assertEquals(10000L, summary.getTotalDeliveredImpressions());
        assertEquals(200L, summary.getTotalClicks());
        assertEquals(new BigDecimal("999.99"), summary.getTotalSpend());
        // 200 / 10000 * 100 = 2.00
        assertEquals(new BigDecimal("2.00"), summary.getCtr());
    }

    @Test
    void summaryForInsertionOrder_excludesNonAcceptedRecords() {
        when(deliveryRepository.findByIoId(20L)).thenReturn(List.of(
                record(1L, 10L, 20L, 30L, 1000L, 10L, "100.00",
                        Source.PublisherReport, DeliveryStatus.Accepted),
                record(2L, 10L, 20L, 30L, 3000L, 20L, "200.50",
                        Source.PublisherReport, DeliveryStatus.Accepted),
                record(3L, 10L, 20L, 30L, 9999L, 999L, "999.99",
                        Source.PublisherReport, DeliveryStatus.Disputed)));

        DeliverySummaryResponse summary = service.summaryForInsertionOrder(20L);

        assertEquals("InsertionOrder", summary.getScope());
        assertEquals(2L, summary.getRecordCount());
        assertEquals(4000L, summary.getTotalDeliveredImpressions());
        assertEquals(30L, summary.getTotalClicks());
        assertEquals(new BigDecimal("300.50"), summary.getTotalSpend());
        // 30 / 4000 * 100 = 0.75
        assertEquals(new BigDecimal("0.75"), summary.getCtr());
    }

    @Test
    void summaryForCampaign_zeroImpressionsYieldsZeroCtr() {
        when(deliveryRepository.findByCampaignBriefId(30L)).thenReturn(List.of(
                record(1L, 10L, 20L, 30L, 0L, 0L, "0.00",
                        Source.PublisherReport, DeliveryStatus.Accepted),
                record(2L, 10L, 20L, 30L, 5000L, 50L, "500.00",
                        Source.PublisherReport, DeliveryStatus.PendingVerification)));

        DeliverySummaryResponse summary = service.summaryForCampaign(30L);

        assertEquals("Campaign", summary.getScope());
        assertEquals(1L, summary.getRecordCount());
        assertEquals(0L, summary.getTotalDeliveredImpressions());
        assertEquals(0L, summary.getTotalClicks());
        assertEquals(new BigDecimal("0.00"), summary.getTotalSpend());
        assertEquals(new BigDecimal("0.00"), summary.getCtr());
    }
}
