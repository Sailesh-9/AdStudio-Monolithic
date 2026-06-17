package com.cts.adstudio.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A unit of delivery reported against a media line item (Backend Plan 4.6).
 * Records the impressions, clicks and spend a publisher delivered on a given
 * reporting date, the source of the figures, and a verification status.
 *
 * <p>Monolith note: the {@code mediaplan} module ships a <em>temporary</em>
 * {@code DeliveryRecord} (default JPA name {@code DeliveryRecord}, table
 * {@code delivery_record}) that it used for standalone pacing tests before this
 * service existed. To coexist in a single persistence unit, this authoritative
 * delivery entity uses a distinct JPA entity name and table. No JPQL references
 * it by name outside this module, so the Java class name stays
 * {@code DeliveryRecord}. This follows the same pattern the monolith already
 * uses for the duplicated {@code AuditLog} entity.</p>
 */
@Entity(name = "DeliveryServiceRecord")
@Table(name = "delivery_service_record", indexes = {
        @Index(name = "idx_dsr_line_item", columnList = "line_item_id"),
        @Index(name = "idx_dsr_io", columnList = "io_id"),
        @Index(name = "idx_dsr_brief", columnList = "campaign_brief_id"),
        @Index(name = "idx_dsr_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    /** Media line item this delivery is reported against (mediaplan LineItemID). */
    @Column(name = "line_item_id", nullable = false)
    private Long lineItemId;

    /** Insertion order this delivery fulfils (mediaplan IOID). Basis for publisher reconciliation. */
    @Column(name = "io_id")
    private Long ioId;

    /**
     * Campaign brief this delivery rolls up to (advertiser CampaignBriefID),
     * denormalized so the delivered-spend rollup the Finance module needs is a
     * single indexed aggregation with no cross-module join.
     */
    @Column(name = "campaign_brief_id")
    private Long campaignBriefId;

    @Column(name = "reporting_date", nullable = false)
    private LocalDate reportingDate;

    @Column(name = "delivered_impressions", nullable = false)
    private Long deliveredImpressions;

    @Column(name = "clicks", nullable = false)
    private Long clicks;

    @Column(name = "spend", precision = 15, scale = 2, nullable = false)
    private BigDecimal spend;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 32)
    private Source source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DeliveryStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.clicks == null) this.clicks = 0L;
        if (this.deliveredImpressions == null) this.deliveredImpressions = 0L;
        if (this.spend == null) this.spend = BigDecimal.ZERO;
        if (this.source == null) this.source = Source.PublisherReport;
        if (this.status == null) this.status = DeliveryStatus.Accepted;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** Where the figures came from. */
    public enum Source {
        PublisherReport, InternalEntry
    }

    /**
     * Verification state. Only {@link #Accepted} delivery counts toward delivered
     * totals, pacing and billing; {@link #Disputed} and {@link #PendingVerification}
     * are excluded until accepted.
     */
    public enum DeliveryStatus {
        Accepted, Disputed, PendingVerification
    }
}
