package com.cts.delivery.service;

import com.cts.delivery.entity.*;
import com.cts.delivery.repository.*;
import com.cts.delivery.dto.DeliveryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepo;
    private final AlertRepository alertRepo;

    public DeliveryRecord simulate(DeliveryRequest req) {

        String status;

        if (req.deliveredImpressions() < req.targetImpressions() * 0.5)
            status = "UnderDelivery";
        else if (req.deliveredImpressions() > req.targetImpressions())
            status = "OverDelivery";
        else
            status = "OnTrack";

        var record = DeliveryRecord.builder()
                .lineItemId(req.lineItemId())
                .targetImpressions(req.targetImpressions())
                .deliveredImpressions(req.deliveredImpressions())
                .pacingStatus(status)
                .build();

        var saved = deliveryRepo.save(record);

        if (!"OnTrack".equals(status)) {
            alertRepo.save(
                    PacingAlert.builder()
                            .lineItemId(req.lineItemId())
                            .alertType(status)
                            .build()
            );
        }

        return saved;
    }
}