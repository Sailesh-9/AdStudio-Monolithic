package com.cts.adstudio.mediaplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "insertion_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsertionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "io_id")
    private Integer ioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_item_id", nullable = false)
    private MediaLineItem mediaLineItem;

    @Column(name = "publisher_id", nullable = false)
    private Integer publisherId;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "committed_impressions")
    private Integer committedImpressions;

    @Column(name = "order_value", precision = 15, scale = 2)
    private BigDecimal orderValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IOStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = IOStatus.Sent;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum IOStatus {
        Sent, Confirmed, Rejected, Delivered, Disputed
    }
}