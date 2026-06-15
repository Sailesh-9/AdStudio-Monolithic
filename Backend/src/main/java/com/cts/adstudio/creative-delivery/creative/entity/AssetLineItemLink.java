package com.cts.creative.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"asset_id","lineItemId"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AssetLineItemLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    private Long lineItemId;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private CreativeAsset asset;
}