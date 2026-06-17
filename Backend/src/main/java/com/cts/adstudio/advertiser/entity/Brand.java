package com.cts.adstudio.advertiser.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="brand")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BrandID")
    private Integer brandId;

    @Column(name = "AdvertiserID", nullable = false)
    private Integer advertiserId;

    @Column(name = "BrandName", nullable = false, length = 100)
    private String brandName;

    @Column(name = "Category", length = 100)
    private String category;

    @Column(name = "AllocatedBudget", precision = 15, scale = 2)
    private BigDecimal allocatedBudget;

    @Column(name = "SpentToDate", precision = 15, scale = 2)
    private BigDecimal spentToDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private BrandStatus status;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = BrandStatus.Active;
        if (this.spentToDate == null) this.spentToDate = BigDecimal.ZERO;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum BrandStatus {
        Active, Discontinued
    }

}
