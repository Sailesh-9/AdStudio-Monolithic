package com.cts.adstudio.mediaplan.shared;

import com.cts.adstudio.mediaplan.entity.InsertionOrder;
import com.cts.adstudio.mediaplan.entity.MediaLineItem;
import com.cts.adstudio.mediaplan.entity.MediaPlan;
import org.springframework.stereotype.Component;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component("mediaplanStatusTransitionValidator")
public class StatusTransitionValidator {

    // Allowed "next states" for each MediaPlan status
    private static final Map<MediaPlan.MediaPlanStatus, Set<MediaPlan.MediaPlanStatus>> PLAN = Map.of(
            MediaPlan.MediaPlanStatus.Draft,           EnumSet.of(MediaPlan.MediaPlanStatus.PendingApproval),
            MediaPlan.MediaPlanStatus.PendingApproval, EnumSet.of(MediaPlan.MediaPlanStatus.Approved, MediaPlan.MediaPlanStatus.Draft),
            MediaPlan.MediaPlanStatus.Approved,        EnumSet.of(MediaPlan.MediaPlanStatus.Active),
            MediaPlan.MediaPlanStatus.Active,          EnumSet.of(MediaPlan.MediaPlanStatus.Completed)
            // Completed = terminal (no key needed)
    );

    // Allowed "next states" for each MediaLineItem status
    private static final Map<MediaLineItem.LineItemStatus, Set<MediaLineItem.LineItemStatus>> LINE_ITEM = Map.of(
            MediaLineItem.LineItemStatus.Planned, EnumSet.of(MediaLineItem.LineItemStatus.Ordered),
            MediaLineItem.LineItemStatus.Ordered, EnumSet.of(MediaLineItem.LineItemStatus.Live),
            MediaLineItem.LineItemStatus.Live,    EnumSet.of(MediaLineItem.LineItemStatus.Paused, MediaLineItem.LineItemStatus.Completed),
            MediaLineItem.LineItemStatus.Paused,  EnumSet.of(MediaLineItem.LineItemStatus.Live, MediaLineItem.LineItemStatus.Completed)
            // Completed = terminal
    );

    // Allowed "next states" for each InsertionOrder status
    private static final Map<InsertionOrder.IOStatus, Set<InsertionOrder.IOStatus>> IO = Map.of(
            InsertionOrder.IOStatus.Sent,      EnumSet.of(InsertionOrder.IOStatus.Confirmed, InsertionOrder.IOStatus.Rejected),
            InsertionOrder.IOStatus.Confirmed, EnumSet.of(InsertionOrder.IOStatus.Delivered, InsertionOrder.IOStatus.Disputed),
            InsertionOrder.IOStatus.Delivered, EnumSet.of(InsertionOrder.IOStatus.Disputed)
            // Rejected, Disputed = terminal
    );

    public void validatePlan(MediaPlan.MediaPlanStatus current, MediaPlan.MediaPlanStatus target) {
        check(current, target, PLAN.getOrDefault(current, EnumSet.noneOf(MediaPlan.MediaPlanStatus.class)));
    }

    public void validateLineItem(MediaLineItem.LineItemStatus current, MediaLineItem.LineItemStatus target) {
        check(current, target, LINE_ITEM.getOrDefault(current, EnumSet.noneOf(MediaLineItem.LineItemStatus.class)));
    }

    public void validateInsertionOrder(InsertionOrder.IOStatus current, InsertionOrder.IOStatus target) {
        check(current, target, IO.getOrDefault(current, EnumSet.noneOf(InsertionOrder.IOStatus.class)));
    }

    // Shared logic for all three
    private <E extends Enum<E>> void check(E current, E target, Set<E> allowed) {
        if (current == target) {
            return; // same status = no change, allowed
        }
        if (!allowed.contains(target)) {
            throw new IllegalArgumentException(String.format(
                    "Illegal status transition: %s -> %s. Allowed next: %s",
                    current, target, allowed.isEmpty() ? "none (terminal state)" : allowed));
        }
    }
}