package com.cts.adstudio.delivery.repository;

import com.cts.adstudio.delivery.entity.PacingAlert;
import com.cts.adstudio.delivery.entity.PacingAlert.AlertStatus;
import com.cts.adstudio.delivery.entity.PacingAlert.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Persistence for {@link PacingAlert}. */
@Repository
public interface AlertRepository extends JpaRepository<PacingAlert, Long> {

    List<PacingAlert> findByStatus(AlertStatus status);

    List<PacingAlert> findByLineItemId(Long lineItemId);

    /** Used to avoid raising a duplicate alert while one of the same type is still open. */
    boolean existsByLineItemIdAndAlertTypeAndStatus(Long lineItemId, AlertType alertType, AlertStatus status);
}
