package com.cts.adstudio.mediaplan.service.impl;

import com.cts.adstudio.mediaplan.dto.request.MediaLineItemRequest;
import com.cts.adstudio.mediaplan.dto.response.MediaLineItemResponse;
import com.cts.adstudio.mediaplan.entity.MediaLineItem;
import com.cts.adstudio.mediaplan.entity.MediaPlan;
import com.cts.adstudio.mediaplan.exception.ResourceNotFoundException;
import com.cts.adstudio.mediaplan.repository.MediaLineItemRepository;
import com.cts.adstudio.mediaplan.repository.MediaPlanRepository;
import com.cts.adstudio.mediaplan.service.MediaLineItemService;
import com.cts.adstudio.mediaplan.shared.StatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaLineItemServiceImpl implements MediaLineItemService {

    private final MediaLineItemRepository lineItemRepository;
    private final MediaPlanRepository mediaPlanRepository;
    private final StatusTransitionValidator statusValidator;

    @Override
    public MediaLineItemResponse createLineItem(Integer planId, MediaLineItemRequest request) {
        // 1. Find the parent media plan (must exist)
        MediaPlan plan = mediaPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Media Plan not found with ID: " + planId));

        // 2. Validate channel
        MediaLineItem.Channel channel = parseChannel(request.getChannel());

        // 3. BUDGET CHECK — new line item must not push total over the plan budget
        validateBudgetWithinPlan(plan, request.getPlannedBudget(), null);

        // 4. Build and save
        MediaLineItem item = MediaLineItem.builder()
                .mediaPlan(plan)
                .channel(channel)
                .publisher(request.getPublisher())
                .format(request.getFormat())
                .plannedImpressions(request.getPlannedImpressions())
                .plannedBudget(request.getPlannedBudget())
                .cpm(request.getCpm())
                .flightStart(request.getFlightStart())
                .flightEnd(request.getFlightEnd())
                .status(MediaLineItem.LineItemStatus.Planned)
                .build();

        MediaLineItem saved = lineItemRepository.save(item);
        log.info("Line item {} created under plan {}", saved.getLineItemId(), planId);
        return mapToResponse(saved);
    }

    @Override
    public List<MediaLineItemResponse> getLineItemsByPlan(Integer planId) {
        // Make sure the plan exists first
        if (!mediaPlanRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Media Plan not found with ID: " + planId);
        }
        return lineItemRepository.findByMediaPlan_PlanId(planId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MediaLineItemResponse getLineItemById(Integer lineItemId) {
        MediaLineItem item = lineItemRepository.findById(lineItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Line Item not found with ID: " + lineItemId));
        return mapToResponse(item);
    }

    @Override
    public MediaLineItemResponse updateLineItem(Integer lineItemId, MediaLineItemRequest request) {
        MediaLineItem item = lineItemRepository.findById(lineItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Line Item not found with ID: " + lineItemId));

        // BUDGET CHECK — exclude THIS line item's old budget from the running total
        validateBudgetWithinPlan(item.getMediaPlan(), request.getPlannedBudget(), lineItemId);

        item.setChannel(parseChannel(request.getChannel()));
        item.setPublisher(request.getPublisher());
        item.setFormat(request.getFormat());
        item.setPlannedImpressions(request.getPlannedImpressions());
        item.setPlannedBudget(request.getPlannedBudget());
        item.setCpm(request.getCpm());
        item.setFlightStart(request.getFlightStart());
        item.setFlightEnd(request.getFlightEnd());

        return mapToResponse(lineItemRepository.save(item));
    }

    @Override
    public MediaLineItemResponse updateLineItemStatus(Integer lineItemId, String newStatus) {
        MediaLineItem item = lineItemRepository.findById(lineItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Line Item not found with ID: " + lineItemId));

        MediaLineItem.LineItemStatus status;
        try {
            status = MediaLineItem.LineItemStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus
                    + ". Allowed: Planned, Ordered, Live, Paused, Completed");
        }

        // enforce legal workflow transition (Planned -> Ordered -> Live -> ...)
        statusValidator.validateLineItem(item.getStatus(), status);

        item.setStatus(status);
        log.info("Line item {} status updated to {}", lineItemId, newStatus);
        return mapToResponse(lineItemRepository.save(item));
    }

    @Override
    public void deleteLineItem(Integer lineItemId) {
        if (!lineItemRepository.existsById(lineItemId)) {
            throw new ResourceNotFoundException("Line Item not found with ID: " + lineItemId);
        }
        lineItemRepository.deleteById(lineItemId);
    }

    // ---- helpers ----

    private MediaLineItem.Channel parseChannel(String channelText) {
        try {
            return MediaLineItem.Channel.valueOf(channelText);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid channel: " + channelText
                    + ". Allowed: Display, Video, Social, Search, OOH, Print, Radio");
        }
    }

    /**
     * Ensures the sum of all line item budgets under a plan does not exceed
     * the plan's total allocated budget.
     *
     * @param plan      the parent media plan
     * @param newBudget the budget being added or updated
     * @param excludeId when updating, the line item's own ID (so we don't
     *                  count its old budget twice). Pass null when creating.
     */
    private void validateBudgetWithinPlan(MediaPlan plan, BigDecimal newBudget, Integer excludeId) {
        BigDecimal planBudget = plan.getTotalBudgetAllocated();
        if (planBudget == null) {
            return; // plan has no budget cap set; skip the check
        }

        // Sum the budgets of all EXISTING line items in this plan
        BigDecimal existingTotal = lineItemRepository
                .findByMediaPlan_PlanId(plan.getPlanId())
                .stream()
                .filter(li -> excludeId == null || !li.getLineItemId().equals(excludeId))
                .map(MediaLineItem::getPlannedBudget)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal projectedTotal = existingTotal.add(newBudget);

        if (projectedTotal.compareTo(planBudget) > 0) {
            throw new IllegalArgumentException(String.format(
                    "Budget exceeded: plan budget is %s, already allocated %s, "
                    + "this line item (%s) would make the total %s.",
                    planBudget, existingTotal, newBudget, projectedTotal));
        }
    }

    private MediaLineItemResponse mapToResponse(MediaLineItem item) {
        return MediaLineItemResponse.builder()
                .lineItemId(item.getLineItemId())
                .planId(item.getMediaPlan() != null ? item.getMediaPlan().getPlanId() : null)
                .channel(item.getChannel() != null ? item.getChannel().name() : null)
                .publisher(item.getPublisher())
                .format(item.getFormat())
                .plannedImpressions(item.getPlannedImpressions())
                .plannedBudget(item.getPlannedBudget())
                .cpm(item.getCpm())
                .flightStart(item.getFlightStart())
                .flightEnd(item.getFlightEnd())
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}