package com.cts.adstudio.mediaplan.service.impl;

import com.cts.adstudio.mediaplan.dto.request.DeliveryRecordRequest;
import com.cts.adstudio.mediaplan.dto.response.DeliveryRecordResponse;
import com.cts.adstudio.mediaplan.entity.DeliveryRecord;
import com.cts.adstudio.mediaplan.exception.ResourceNotFoundException;
import com.cts.adstudio.mediaplan.repository.DeliveryRecordRepository;
import com.cts.adstudio.mediaplan.repository.MediaLineItemRepository;
import com.cts.adstudio.mediaplan.service.DeliveryRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryRecordServiceImpl implements DeliveryRecordService {

    private final DeliveryRecordRepository deliveryRepository;
    private final MediaLineItemRepository lineItemRepository;

    @Override
    public DeliveryRecordResponse recordDelivery(DeliveryRecordRequest request) {
        // The line item being delivered must exist
        if (!lineItemRepository.existsById(request.getLineItemId())) {
            throw new ResourceNotFoundException(
                    "Line Item not found with ID: " + request.getLineItemId());
        }

        DeliveryRecord record = DeliveryRecord.builder()
                .lineItemId(request.getLineItemId())
                .reportingDate(request.getReportingDate())
                .deliveredImpressions(request.getDeliveredImpressions())
                .spend(request.getSpend())
                .build();

        DeliveryRecord saved = deliveryRepository.save(record);
        log.info("Delivery recorded for line item {}: {} impressions, spend {}",
                request.getLineItemId(), request.getDeliveredImpressions(), request.getSpend());
        return mapToResponse(saved);
    }

    @Override
    public List<DeliveryRecordResponse> getDeliveryByLineItem(Integer lineItemId) {
        return deliveryRepository.findAll().stream()
                .filter(d -> d.getLineItemId().equals(lineItemId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DeliveryRecordResponse mapToResponse(DeliveryRecord d) {
        return DeliveryRecordResponse.builder()
                .deliveryId(d.getDeliveryId())
                .lineItemId(d.getLineItemId())
                .reportingDate(d.getReportingDate())
                .deliveredImpressions(d.getDeliveredImpressions())
                .spend(d.getSpend())
                .createdAt(d.getCreatedAt())
                .build();
    }
}