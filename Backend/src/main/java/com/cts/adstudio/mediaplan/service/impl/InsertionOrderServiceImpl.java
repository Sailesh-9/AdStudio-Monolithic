package com.cts.adstudio.mediaplan.service.impl;

import com.cts.adstudio.mediaplan.dto.request.InsertionOrderRequest;
import com.cts.adstudio.mediaplan.dto.response.InsertionOrderResponse;
import com.cts.adstudio.mediaplan.entity.InsertionOrder;
import com.cts.adstudio.mediaplan.entity.MediaLineItem;
import com.cts.adstudio.mediaplan.exception.ResourceNotFoundException;
import com.cts.adstudio.mediaplan.repository.InsertionOrderRepository;
import com.cts.adstudio.mediaplan.repository.MediaLineItemRepository;
import com.cts.adstudio.mediaplan.service.InsertionOrderService;
import com.cts.adstudio.mediaplan.shared.AuditLogService;
import com.cts.adstudio.mediaplan.shared.StatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsertionOrderServiceImpl implements InsertionOrderService {

    private final InsertionOrderRepository ioRepository;
    private final MediaLineItemRepository lineItemRepository;
    private final AuditLogService auditLogService;            // shared utility
    private final StatusTransitionValidator statusValidator;  // shared utility

    @Override
    public InsertionOrderResponse createInsertionOrder(InsertionOrderRequest request) {
        // 1. The line item this IO is for must exist
        MediaLineItem lineItem = lineItemRepository.findById(request.getLineItemId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Line Item not found with ID: " + request.getLineItemId()));

        // 2. Build the IO (status defaults to Sent)
        InsertionOrder io = InsertionOrder.builder()
                .mediaLineItem(lineItem)
                .publisherId(request.getPublisherId())
                .orderDate(request.getOrderDate())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .committedImpressions(request.getCommittedImpressions())
                .orderValue(request.getOrderValue())
                .status(InsertionOrder.IOStatus.Sent)
                .build();

        InsertionOrder saved = ioRepository.save(io);

        // 3. Audit: record that an IO was created and sent
        auditLogService.log(request.getPublisherId(), "IO_CREATED_AND_SENT",
                "InsertionOrder", saved.getIoId());

        log.info("Insertion Order {} created for line item {}",
                saved.getIoId(), request.getLineItemId());
        return mapToResponse(saved);
    }

    @Override
    public InsertionOrderResponse getInsertionOrderById(Integer ioId) {
        InsertionOrder io = ioRepository.findById(ioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Insertion Order not found with ID: " + ioId));
        return mapToResponse(io);
    }

    @Override
    public List<InsertionOrderResponse> getAllInsertionOrders() {
        return ioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsertionOrderResponse> getInsertionOrdersByPublisher(Integer publisherId) {
        return ioRepository.findByPublisherId(publisherId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InsertionOrderResponse updateStatus(Integer ioId, String newStatus) {
        InsertionOrder io = ioRepository.findById(ioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Insertion Order not found with ID: " + ioId));

        InsertionOrder.IOStatus status;
        try {
            status = InsertionOrder.IOStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus
                    + ". Allowed: Sent, Confirmed, Rejected, Delivered, Disputed");
        }

        // enforce legal workflow transition (Sent -> Confirmed/Rejected -> ...)
        statusValidator.validateInsertionOrder(io.getStatus(), status);

        io.setStatus(status);
        InsertionOrder saved = ioRepository.save(io);

        // AUDIT every IO status change (dev plan requirement)
        auditLogService.log(io.getPublisherId(),
                "IO_STATUS_CHANGED_TO_" + newStatus,
                "InsertionOrder", ioId);

        log.info("Insertion Order {} status changed to {}", ioId, newStatus);
        return mapToResponse(saved);
    }

    private InsertionOrderResponse mapToResponse(InsertionOrder io) {
        return InsertionOrderResponse.builder()
                .ioId(io.getIoId())
                .lineItemId(io.getMediaLineItem() != null ? io.getMediaLineItem().getLineItemId() : null)
                .publisherId(io.getPublisherId())
                .orderDate(io.getOrderDate())
                .startDate(io.getStartDate())
                .endDate(io.getEndDate())
                .committedImpressions(io.getCommittedImpressions())
                .orderValue(io.getOrderValue())
                .status(io.getStatus() != null ? io.getStatus().name() : null)
                .createdAt(io.getCreatedAt())
                .updatedAt(io.getUpdatedAt())
                .build();
    }
}