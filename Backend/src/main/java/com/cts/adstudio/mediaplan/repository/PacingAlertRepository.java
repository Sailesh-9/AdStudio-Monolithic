package com.cts.adstudio.mediaplan.repository;

import com.cts.adstudio.mediaplan.entity.PacingAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PacingAlertRepository extends JpaRepository<PacingAlert, Integer> {
    List<PacingAlert> findByStatus(PacingAlert.AlertStatus status);
    List<PacingAlert> findByLineItemId(Integer lineItemId);

    // Used to avoid creating duplicate open alerts of the same type for a line item
    boolean existsByLineItemIdAndAlertTypeAndStatus(
            Integer lineItemId, PacingAlert.AlertType alertType, PacingAlert.AlertStatus status);
}