package com.cts.adstudio.mediaplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacing_alert")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacingAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Integer alertId;

    @Column(name = "line_item_id", nullable = false)
    private Integer lineItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(name = "alert_date")
    private LocalDate alertDate;

    @Column(name = "pacing_percent", precision = 6, scale = 2)
    private BigDecimal pacingPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlertStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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