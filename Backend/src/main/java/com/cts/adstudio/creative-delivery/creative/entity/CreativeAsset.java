package com.cts.creative.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"assetName","brandId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreativeAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;

    private Long brandId;

    private String assetName;

    private String filePath;

    private Integer width;
    private Integer height;

    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonIgnore
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<CreativeApproval> approvals;

    @JsonIgnore
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<AssetLineItemLink> links;

    public enum Status {
        Draft, Approved, Rejected
    }

    public enum AssetType {
        BANNER, VIDEO, IMAGE, NATIVE, AUDIO
    }
}