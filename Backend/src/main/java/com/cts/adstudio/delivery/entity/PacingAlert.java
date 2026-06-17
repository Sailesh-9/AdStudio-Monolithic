package com.cts.adstudio.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A pacing exception raised against a media line item (Backend Plan 4.6):
 * under/over delivery, exhausted budget, or an approaching flight end. The
 * status workflow (Open -> Actioned -> Closed) is how make-good handling is
 * tracked: an under-delivery alert is Actioned when corrective delivery is
 * arranged and Closed once resolved.
 *
 * <p>Monolith note: as with {@link DeliveryRecord}, the {@code mediaplan} module
 * has a temporary {@code PacingAlert} (table {@code pacing_alert}). This
 * authoritative entity uses a distinct JPA entity name and table to coexist in
 * the single persistence unit while keeping the Java class name {@code PacingAlert}.</p>
 */
@Entity(name = "DeliveryServicePacingAlert")
@Table(name = "delivery_service_pacing_alert", indexes = {
        @Index(name = "idx_dspa_line_item", columnList = "line_item_id"),
        @Index(name = "idx_dspa_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacingAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "line_item_id", nullable = false)
    private Long lineItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 32)
    private AlertType alertType;

    @Column(name = "alert_date")
    private LocalDate alertDate;

    /** Delivered-vs-expected pacing as a percentage; null for non-pacing alert types. */
    @Column(name = "pacing_percent", precision = 6, scale = 2)
    private BigDecimal pacingPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private AlertStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = AlertStatus.Open;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum AlertType {
        UnderDelivery, OverDelivery, BudgetExhausted, FlightEndApproaching
    }

    public enum AlertStatus {
        Open, Actioned, Closed
    }
}
