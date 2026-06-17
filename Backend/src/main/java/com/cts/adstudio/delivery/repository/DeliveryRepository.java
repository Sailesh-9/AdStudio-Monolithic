package com.cts.adstudio.delivery.repository;

import com.cts.adstudio.delivery.entity.DeliveryRecord;
import com.cts.adstudio.delivery.entity.DeliveryRecord.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Persistence for {@link DeliveryRecord}. All aggregation queries reference the
 * entity by its distinct JPA name ({@code DeliveryServiceRecord}) and are
 * null-safe via {@code COALESCE}, so they return {@code 0} rather than
 * {@code null} when there is no matching delivery.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryRecord, Long> {

    List<DeliveryRecord> findByLineItemId(Long lineItemId);

    List<DeliveryRecord> findByIoId(Long ioId);

    List<DeliveryRecord> findByCampaignBriefId(Long campaignBriefId);

    List<DeliveryRecord> findByStatus(DeliveryStatus status);

    // ---- Finance contract: spend rolled up by campaign / insertion order -----

    /** Total spend for a campaign brief at the given status (basis for the client invoice). */
    @Query("SELECT COALESCE(SUM(d.spend), 0) FROM DeliveryServiceRecord d " +
           "WHERE d.campaignBriefId = :briefId AND d.status = :status")
    BigDecimal sumSpendByCampaignAndStatus(@Param("briefId") Long briefId,
                                           @Param("status") DeliveryStatus status);

    /** Total spend for an insertion order at the given status (basis for publisher reconciliation). */
    @Query("SELECT COALESCE(SUM(d.spend), 0) FROM DeliveryServiceRecord d " +
           "WHERE d.ioId = :ioId AND d.status = :status")
    BigDecimal sumSpendByIoAndStatus(@Param("ioId") Long ioId,
                                     @Param("status") DeliveryStatus status);

    // ---- Performance rollups by line item ------------------------------------

    @Query("SELECT COALESCE(SUM(d.deliveredImpressions), 0) FROM DeliveryServiceRecord d " +
           "WHERE d.lineItemId = :lineItemId AND d.status = :status")
    long sumImpressionsByLineItemAndStatus(@Param("lineItemId") Long lineItemId,
                                           @Param("status") DeliveryStatus status);

    @Query("SELECT COALESCE(SUM(d.clicks), 0) FROM DeliveryServiceRecord d " +
           "WHERE d.lineItemId = :lineItemId AND d.status = :status")
    long sumClicksByLineItemAndStatus(@Param("lineItemId") Long lineItemId,
                                      @Param("status") DeliveryStatus status);

    @Query("SELECT COALESCE(SUM(d.spend), 0) FROM DeliveryServiceRecord d " +
           "WHERE d.lineItemId = :lineItemId AND d.status = :status")
    BigDecimal sumSpendByLineItemAndStatus(@Param("lineItemId") Long lineItemId,
                                           @Param("status") DeliveryStatus status);

    @Query("SELECT COUNT(d) FROM DeliveryServiceRecord d " +
           "WHERE d.lineItemId = :lineItemId AND d.status = :status")
    long countByLineItemAndStatus(@Param("lineItemId") Long lineItemId,
                                  @Param("status") DeliveryStatus status);
}
