package com.cts.adstudio.delivery.service;

import com.cts.adstudio.delivery.dto.DeliveryRecordResponse;
import com.cts.adstudio.delivery.dto.DeliveryRequest;
import com.cts.adstudio.delivery.dto.DeliverySummaryResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Delivery recording, status management, performance rollups, and the two
 * delivered-figure aggregations the Finance module consumes over HTTP.
 */
public interface DeliveryService {

    DeliveryRecordResponse recordDelivery(DeliveryRequest request);

    DeliveryRecordResponse getById(Long deliveryId);

    List<DeliveryRecordResponse> getByLineItem(Long lineItemId);

    List<DeliveryRecordResponse> getByInsertionOrder(Long ioId);

    List<DeliveryRecordResponse> getByCampaign(Long campaignBriefId);

    List<DeliveryRecordResponse> getByStatus(String status);

    DeliveryRecordResponse updateStatus(Long deliveryId, String newStatus);

    /** Finance contract: total accepted delivered spend for a campaign brief. */
    BigDecimal deliveredSpendForCampaign(Long campaignBriefId);

    /** Finance contract: accepted delivered value for an insertion order. */
    BigDecimal deliveredValueForInsertionOrder(Long ioId);

    DeliverySummaryResponse summaryForLineItem(Long lineItemId);

    DeliverySummaryResponse summaryForInsertionOrder(Long ioId);

    DeliverySummaryResponse summaryForCampaign(Long campaignBriefId);
}
