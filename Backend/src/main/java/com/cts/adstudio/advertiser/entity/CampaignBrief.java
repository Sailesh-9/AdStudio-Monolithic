package com.cts.adstudio.advertiser.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaignbrief")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignBrief {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BriefID")
    private Integer briefId;

    @Column(name = "BrandID", nullable = false)
    private Integer brandId;

    @Column(name = "CampaignName", nullable = false, length = 150)
    private String campaignName;

    @Enumerated(EnumType.STRING)
    @Column(name = "Objective")
    private CampaignObjective objective;

    @Column(name = "TargetDemographic", columnDefinition = "TEXT")
    private String targetDemographic;

    @Column(name = "Geography", length = 200)
    private String geography;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "TotalBudget", precision = 15, scale = 2)
    private BigDecimal totalBudget;

    @Column(name = "ChannelPreferences", columnDefinition = "TEXT")
    private String channelPreferences;

    @Column(name = "SubmittedById")
    private Integer submittedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private CampaignStatus status;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = CampaignStatus.Draft;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum CampaignObjective {
        Awareness, Consideration, Conversion, Retention
    }

    public enum CampaignStatus {
        Draft, Submitted, Approved, Rejected, Active, Completed
    }
}