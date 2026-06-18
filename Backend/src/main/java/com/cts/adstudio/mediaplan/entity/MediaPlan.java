package com.cts.adstudio.mediaplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "media_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "brief_id", nullable = false)
    private Integer briefId;

    @Column(name = "planner_id", nullable = false)
    private Integer plannerId;

    @Column(name = "total_budget_allocated", precision = 15, scale = 2)
    private BigDecimal totalBudgetAllocated;

    @Column(name = "channel_mix", columnDefinition = "TEXT")
    private String channelMix;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MediaPlanStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "mediaPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MediaLineItem> lineItems;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MediaPlanStatus.Draft;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum MediaPlanStatus {
        Draft, PendingApproval, Approved, Active, Completed
    }
}