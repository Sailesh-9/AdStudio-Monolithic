package com.cts.adstudio.mediaplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// TEMPORARY entity for standalone pacing testing.
// Replaced by Dev 4's delivery-service during integration.
@Entity
@Table(name = "delivery_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Integer deliveryId;

    @Column(name = "line_item_id", nullable = false)
    private Integer lineItemId;

    @Column(name = "reporting_date")
    private LocalDate reportingDate;

    @Column(name = "delivered_impressions")
    private Integer deliveredImpressions;

    @Column(name = "spend", precision = 15, scale = 2)
    private BigDecimal spend;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}