package com.cts.adstudio.mediaplan.repository;

import com.cts.adstudio.mediaplan.entity.DeliveryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRecordRepository extends JpaRepository<DeliveryRecord, Integer> {

    // Total delivered impressions so far for a line item (null-safe via COALESCE)
    @Query("SELECT COALESCE(SUM(d.deliveredImpressions), 0) FROM DeliveryRecord d " +
           "WHERE d.lineItemId = :lineItemId")
    Long sumDeliveredImpressions(@Param("lineItemId") Integer lineItemId);

    // Total spend so far for a line item
    @Query("SELECT COALESCE(SUM(d.spend), 0) FROM DeliveryRecord d " +
           "WHERE d.lineItemId = :lineItemId")
    java.math.BigDecimal sumSpend(@Param("lineItemId") Integer lineItemId);
}