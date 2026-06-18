package com.cts.adstudio.mediaplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "media_line_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_item_id")
    private Integer lineItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MediaPlan mediaPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private Channel channel;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "format")
    private String format;

    @Column(name = "planned_impressions")
    private Integer plannedImpressions;

    @Column(name = "planned_budget", precision = 15, scale = 2)
    private BigDecimal plannedBudget;

    @Column(name = "cpm", precision = 8, scale = 2)
    private BigDecimal cpm;

    @Column(name = "flight_start")
    private LocalDate flightStart;

    @Column(name = "flight_end")
    private LocalDate flightEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LineItemStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "mediaLineItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InsertionOrder> insertionOrders;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = LineItemStatus.Planned;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Channel {
        Display, Video, Social, Search, OOH, Print, Radio
    }

    public enum LineItemStatus {
        Planned, Ordered, Live, Paused, Completed
    }
}