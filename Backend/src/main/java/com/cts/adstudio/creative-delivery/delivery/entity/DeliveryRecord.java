package com.cts.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lineItemId;
    private Integer targetImpressions;
    private Integer deliveredImpressions;
    private String pacingStatus;
}