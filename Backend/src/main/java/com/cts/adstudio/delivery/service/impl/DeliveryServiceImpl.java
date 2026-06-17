package com.cts.adstudio.delivery.service.impl;

import com.cts.adstudio.delivery.dto.DeliveryRecordResponse;
import com.cts.adstudio.delivery.dto.DeliveryRequest;
import com.cts.adstudio.delivery.dto.DeliverySummaryResponse;
import com.cts.adstudio.delivery.entity.DeliveryRecord;
import com.cts.adstudio.delivery.entity.DeliveryRecord.DeliveryStatus;
import com.cts.adstudio.delivery.entity.DeliveryRecord.Source;
import com.cts.adstudio.delivery.deliveryexception.DeliveryNotFoundException;
import com.cts.adstudio.delivery.repository.DeliveryRepository;
import com.cts.adstudio.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Default {@link DeliveryService} implementation.
 *
 * <p>Delivered totals follow one consistent rule across the module: only
 * {@link DeliveryStatus#Accepted} records count. The two finance-facing methods
 * therefore aggregate accepted spend, matching what the Finance module expects
 * ("total accepted delivered spend / value").</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    /** The single status that counts toward delivered totals, pacing and billing. */
    static final DeliveryStatus COUNTED = DeliveryStatus.Accepted;

    private final DeliveryRepository deliveryRepository;

    // ---- writes --------------------------------------------------------------

    @Override
    @Transactional
    public DeliveryRecordResponse recordDelivery(DeliveryRequest request) {
        DeliveryRecord record = DeliveryRecord.builder()
                .lineItemId(request.getLineItemId())
                .ioId(request.getIoId())
                .campaignBriefId(request.getCampaignBriefId())
                .reportingDate(request.getReportingDate())
                .deliveredImpressions(request.getDeliveredImpressions())
                .clicks(request.getClicks())
                .spend(request.getSpend())
                .source(parseSource(request.getSource()))
                .status(parseStatus(request.getStatus()))
                .build();

        DeliveryRecord saved = deliveryRepository.save(record);
        log.info("Delivery recorded id={} lineItem={} io={} brief={} impressions={} clicks={} spend={} status={}",
                saved.getDeliveryId(), saved.getLineItemId(), saved.getIoId(), saved.getCampaignBriefId(),
                saved.getDeliveredImpressions(), saved.getClicks(), saved.getSpend(), saved.getStatus());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DeliveryRecordResponse updateStatus(Long deliveryId, String newStatus) {
        DeliveryRecord record = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery record not found with ID: " + deliveryId));
        record.setStatus(requireStatus(newStatus));
        DeliveryRecord saved = deliveryRepository.save(record);
        log.info("Delivery {} status changed to {}", deliveryId, saved.getStatus());
        return mapToResponse(saved);
    }

    // ---- reads ---------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public DeliveryRecordResponse getById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery record not found with ID: " + deliveryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryRecordResponse> getByLineItem(Long lineItemId) {
        return deliveryRepository.findByLineItemId(lineItemId).stream()
                .map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryRecordResponse> getByInsertionOrder(Long ioId) {
        return deliveryRepository.findByIoId(ioId).stream()
                .map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryRecordResponse> getByCampaign(Long campaignBriefId) {
        return deliveryRepository.findByCampaignBriefId(campaignBriefId).stream()
                .map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryRecordResponse> getByStatus(String status) {
        return deliveryRepository.findByStatus(requireStatus(status)).stream()
                .map(this::mapToResponse).toList();
    }

    // ---- finance contract ----------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public BigDecimal deliveredSpendForCampaign(Long campaignBriefId) {
        return deliveryRepository.sumSpendByCampaignAndStatus(campaignBriefId, COUNTED);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal deliveredValueForInsertionOrder(Long ioId) {
        return deliveryRepository.sumSpendByIoAndStatus(ioId, COUNTED);
    }

    // ---- performance summaries -----------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public DeliverySummaryResponse summaryForLineItem(Long lineItemId) {
        long impressions = deliveryRepository.sumImpressionsByLineItemAndStatus(lineItemId, COUNTED);
        long clicks = deliveryRepository.sumClicksByLineItemAndStatus(lineItemId, COUNTED);
        BigDecimal spend = deliveryRepository.sumSpendByLineItemAndStatus(lineItemId, COUNTED);
        long count = deliveryRepository.countByLineItemAndStatus(lineItemId, COUNTED);
        return DeliverySummaryResponse.builder()
                .scope("LineItem")
                .scopeId(lineItemId)
                .recordCount(count)
                .totalDeliveredImpressions(impressions)
                .totalClicks(clicks)
                .totalSpend(spend == null ? BigDecimal.ZERO : spend)
                .ctr(ctr(clicks, impressions))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DeliverySummaryResponse summaryForInsertionOrder(Long ioId) {
        return summarise("InsertionOrder", ioId, deliveryRepository.findByIoId(ioId));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliverySummaryResponse summaryForCampaign(Long campaignBriefId) {
        return summarise("Campaign", campaignBriefId,
                deliveryRepository.findByCampaignBriefId(campaignBriefId));
    }

    // ---- helpers -------------------------------------------------------------

    /** Reduce a record list (accepted-only) into a summary. Used for IO/campaign scopes. */
    private DeliverySummaryResponse summarise(String scope, Long scopeId, List<DeliveryRecord> records) {
        long impressions = 0L;
        long clicks = 0L;
        long count = 0L;
        BigDecimal spend = BigDecimal.ZERO;
        for (DeliveryRecord r : records) {
            if (r.getStatus() != COUNTED) continue;
            count++;
            impressions += r.getDeliveredImpressions() == null ? 0L : r.getDeliveredImpressions();
            clicks += r.getClicks() == null ? 0L : r.getClicks();
            spend = spend.add(r.getSpend() == null ? BigDecimal.ZERO : r.getSpend());
        }
        return DeliverySummaryResponse.builder()
                .scope(scope)
                .scopeId(scopeId)
                .recordCount(count)
                .totalDeliveredImpressions(impressions)
                .totalClicks(clicks)
                .totalSpend(spend)
                .ctr(ctr(clicks, impressions))
                .build();
    }

    /** Click-through rate as a percentage, 2dp; 0 when there are no impressions. */
    private BigDecimal ctr(long clicks, long impressions) {
        if (impressions <= 0L) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(clicks)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(impressions), 2, RoundingMode.HALF_UP);
    }

    private DeliveryRecordResponse mapToResponse(DeliveryRecord d) {
        long impressions = d.getDeliveredImpressions() == null ? 0L : d.getDeliveredImpressions();
        long clicks = d.getClicks() == null ? 0L : d.getClicks();
        return DeliveryRecordResponse.builder()
                .deliveryId(d.getDeliveryId())
                .lineItemId(d.getLineItemId())
                .ioId(d.getIoId())
                .campaignBriefId(d.getCampaignBriefId())
                .reportingDate(d.getReportingDate())
                .deliveredImpressions(impressions)
                .clicks(clicks)
                .spend(d.getSpend())
                .ctr(ctr(clicks, impressions))
                .source(d.getSource() != null ? d.getSource().name() : null)
                .status(d.getStatus() != null ? d.getStatus().name() : null)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    /** Optional source; null/blank leaves the entity default (PublisherReport). */
    private Source parseSource(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Source.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid source: " + value + ". Allowed: PublisherReport, InternalEntry");
        }
    }

    /** Optional status; null/blank leaves the entity default (Accepted). */
    private DeliveryStatus parseStatus(String value) {
        if (value == null || value.isBlank()) return null;
        return requireStatus(value);
    }

    /** Required status; throws a 400-mapped error on an unknown value. */
    private DeliveryStatus requireStatus(String value) {
        try {
            return DeliveryStatus.valueOf(value.trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + value + ". Allowed: Accepted, Disputed, PendingVerification");
        }
    }
}
