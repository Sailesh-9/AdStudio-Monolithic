package com.cts.adstudio.delivery.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Aggregated, accepted-only delivery performance for a scope (a line item, an
 * insertion order, or a campaign brief). Backs the performance-tracking reads
 * (impressions, clicks, spend, CTR) in Backend Plan 4.6 / 4.8.
 */
@Data
@Builder
public class DeliverySummaryResponse {

    /** What the figures are scoped to: "LineItem", "InsertionOrder" or "Campaign". */
    private String scope;

    /** Id of the scoped entity (lineItemId / ioId / campaignBriefId). */
    private Long scopeId;

    private long recordCount;
    private long totalDeliveredImpressions;
    private long totalClicks;
    private BigDecimal totalSpend;

    /** Click-through rate as a percentage: clicks / impressions * 100. */
    private BigDecimal ctr;
}
