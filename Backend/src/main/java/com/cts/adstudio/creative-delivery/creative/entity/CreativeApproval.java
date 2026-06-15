package com.cts.creative.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreativeApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;

    private String decision;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private CreativeAsset asset;
}