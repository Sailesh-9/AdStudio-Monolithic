package com.cts.adstudio.advertiser.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "targetaudience")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetAudience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AudienceID")
    private Integer audienceId;

    @Column(name = "BriefID", nullable = false)
    private Integer briefId;

    @Column(name = "AgeRange", length = 50)
    private String ageRange;

    @Column(name = "Gender", length = 50)
    private String gender;

    @Column(name = "Interests", columnDefinition = "TEXT")
    private String interests;

    @Column(name = "Geography", length = 200)
    private String geography;

    @Enumerated(EnumType.STRING)
    @Column(name = "DeviceType")
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private AudienceStatus status;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = AudienceStatus.Active;
        if (this.deviceType == null) this.deviceType = DeviceType.All;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum DeviceType {
        Desktop, Mobile, Tablet, CTV, All
    }

    public enum AudienceStatus {
        Active, Archived
    }
}