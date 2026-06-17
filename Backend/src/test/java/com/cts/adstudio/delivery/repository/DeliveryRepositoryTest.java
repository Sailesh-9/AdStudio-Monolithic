package com.cts.adstudio.delivery.repository;

import com.cts.adstudio.delivery.entity.DeliveryRecord;
import com.cts.adstudio.delivery.entity.DeliveryRecord.DeliveryStatus;
import com.cts.adstudio.delivery.entity.DeliveryRecord.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JPA slice test for {@link DeliveryRepository}. Unlike the service- and
 * controller-layer tests (which mock the repository), this one boots a real
 * persistence unit against the in-memory H2 database configured in MySQL mode
 * by {@code src/test/resources/application.properties}, so it verifies that the
 * hand-written JPQL actually executes and that the {@code COALESCE} aggregations
 * are null-safe.
 *
 * <p>{@code replace = NONE} keeps the explicitly configured H2 datasource rather
 * than letting {@code @DataJpaTest} swap in a default embedded one &mdash; the
 * {@code MODE=MySQL} flag is what lets the production DDL map cleanly.</p>
 *
 * <p>Fixture (all spend values DECIMAL(15,2)):</p>
 * <pre>
 *  Rec | lineItem | io  | brief | impr | clicks | spend   | status
 *  A   | 100      | 200 | 500   | 1000 | 50     | 300.00  | Accepted
 *  B   | 100      | 200 | 500   | 2000 | 100    | 600.00  | Accepted
 *  C   | 100      | 200 | 500   | 9999 | 999    | 999.99  | Disputed
 *  D   | 100      | 201 | 500   | 500  | 25     | 150.00  | PendingVerification
 *  E   | 101      | 202 | 501   | 4000 | 200    | 1000.00 | Accepted
 * </pre>
 * Only {@code Accepted} rows (A, B, E) count toward the delivered rollups, so
 * for line item 100 the accepted totals come from A + B only.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void seed() {
        deliveryRepository.deleteAll();
        persist(100L, 200L, 500L, 1000L, 50L, "300.00", DeliveryStatus.Accepted);            // A
        persist(100L, 200L, 500L, 2000L, 100L, "600.00", DeliveryStatus.Accepted);           // B
        persist(100L, 200L, 500L, 9999L, 999L, "999.99", DeliveryStatus.Disputed);           // C
        persist(100L, 201L, 500L, 500L, 25L, "150.00", DeliveryStatus.PendingVerification);  // D
        persist(101L, 202L, 501L, 4000L, 200L, "1000.00", DeliveryStatus.Accepted);          // E
        deliveryRepository.flush();
    }

    // ---- accepted-only performance rollups by line item ----------------------

    @Test
    void sumImpressionsByLineItemAndStatus_countsAcceptedOnly() {
        assertEquals(3000L, deliveryRepository.sumImpressionsByLineItemAndStatus(100L, DeliveryStatus.Accepted));
    }

    @Test
    void sumClicksByLineItemAndStatus_countsAcceptedOnly() {
        assertEquals(150L, deliveryRepository.sumClicksByLineItemAndStatus(100L, DeliveryStatus.Accepted));
    }

    @Test
    void sumSpendByLineItemAndStatus_countsAcceptedOnly() {
        assertAmount("900.00", deliveryRepository.sumSpendByLineItemAndStatus(100L, DeliveryStatus.Accepted));
    }

    @Test
    void countByLineItemAndStatus_isPerStatus() {
        assertEquals(2L, deliveryRepository.countByLineItemAndStatus(100L, DeliveryStatus.Accepted));
        assertEquals(1L, deliveryRepository.countByLineItemAndStatus(100L, DeliveryStatus.Disputed));
        assertEquals(1L, deliveryRepository.countByLineItemAndStatus(100L, DeliveryStatus.PendingVerification));
    }

    @Test
    void nonAcceptedRowsAreReachableByTheirOwnStatus() {
        // The disputed row C is excluded from the Accepted sums above but still
        // queryable on its own status, proving the status filter is real.
        assertEquals(9999L, deliveryRepository.sumImpressionsByLineItemAndStatus(100L, DeliveryStatus.Disputed));
    }

    // ---- finance contract: spend by campaign / insertion order ---------------

    @Test
    void sumSpendByCampaignAndStatus_acceptedExcludesDisputedAndPending() {
        assertAmount("900.00", deliveryRepository.sumSpendByCampaignAndStatus(500L, DeliveryStatus.Accepted));
    }

    @Test
    void sumSpendByCampaignAndStatus_isPerStatus() {
        assertAmount("999.99", deliveryRepository.sumSpendByCampaignAndStatus(500L, DeliveryStatus.Disputed));
    }

    @Test
    void sumSpendByIoAndStatus_acceptedExcludesDisputed() {
        // IO 200 holds A, B (Accepted) and C (Disputed) -> accepted spend is A + B.
        assertAmount("900.00", deliveryRepository.sumSpendByIoAndStatus(200L, DeliveryStatus.Accepted));
    }

    @Test
    void sumSpendByIoAndStatus_isPerStatus() {
        // IO 201 holds only D, which is PendingVerification.
        assertAmount("0", deliveryRepository.sumSpendByIoAndStatus(201L, DeliveryStatus.Accepted));
        assertAmount("150.00", deliveryRepository.sumSpendByIoAndStatus(201L, DeliveryStatus.PendingVerification));
    }

    // ---- COALESCE null-safety: no matching rows returns 0, never null --------

    @Test
    void aggregatesReturnZeroNotNullWhenNothingMatches() {
        assertEquals(0L, deliveryRepository.sumImpressionsByLineItemAndStatus(999L, DeliveryStatus.Accepted));
        assertEquals(0L, deliveryRepository.sumClicksByLineItemAndStatus(999L, DeliveryStatus.Accepted));
        assertEquals(0L, deliveryRepository.countByLineItemAndStatus(999L, DeliveryStatus.Accepted));

        BigDecimal lineSpend = deliveryRepository.sumSpendByLineItemAndStatus(999L, DeliveryStatus.Accepted);
        assertNotNull(lineSpend);
        assertAmount("0", lineSpend);

        BigDecimal campaignSpend = deliveryRepository.sumSpendByCampaignAndStatus(999L, DeliveryStatus.Accepted);
        assertNotNull(campaignSpend);
        assertAmount("0", campaignSpend);

        BigDecimal ioSpend = deliveryRepository.sumSpendByIoAndStatus(999L, DeliveryStatus.Accepted);
        assertNotNull(ioSpend);
        assertAmount("0", ioSpend);
    }

    // ---- derived finders ------------------------------------------------------

    @Test
    void findByLineItemId_returnsEveryStatusForThatLineItem() {
        assertEquals(4, deliveryRepository.findByLineItemId(100L).size()); // A, B, C, D
        assertEquals(1, deliveryRepository.findByLineItemId(101L).size()); // E
        assertTrue(deliveryRepository.findByLineItemId(999L).isEmpty());
    }

    @Test
    void findByIoId_scopesToInsertionOrder() {
        assertEquals(3, deliveryRepository.findByIoId(200L).size()); // A, B, C
        assertEquals(1, deliveryRepository.findByIoId(201L).size()); // D
    }

    @Test
    void findByCampaignBriefId_scopesToBrief() {
        assertEquals(4, deliveryRepository.findByCampaignBriefId(500L).size()); // A, B, C, D
        assertEquals(1, deliveryRepository.findByCampaignBriefId(501L).size()); // E
    }

    @Test
    void findByStatus_scopesToStatus() {
        assertEquals(3, deliveryRepository.findByStatus(DeliveryStatus.Accepted).size());           // A, B, E
        assertEquals(1, deliveryRepository.findByStatus(DeliveryStatus.Disputed).size());           // C
        assertEquals(1, deliveryRepository.findByStatus(DeliveryStatus.PendingVerification).size()); // D
    }

    // ---- entity @PrePersist contract the rollups depend on -------------------

    @Test
    void prePersistDefaultsMissingCountsStatusAndSource() {
        // Only the two non-defaulted NOT NULL columns are set; @PrePersist must
        // supply the rest, otherwise the NOT NULL inserts would fail.
        DeliveryRecord minimal = DeliveryRecord.builder()
                .lineItemId(700L)
                .reportingDate(LocalDate.of(2025, 2, 1))
                .build();

        DeliveryRecord saved = deliveryRepository.saveAndFlush(minimal);

        assertNotNull(saved.getDeliveryId());
        assertEquals(0L, saved.getDeliveredImpressions());
        assertEquals(0L, saved.getClicks());
        assertAmount("0", saved.getSpend());
        assertEquals(Source.PublisherReport, saved.getSource());
        assertEquals(DeliveryStatus.Accepted, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    // ---- helpers --------------------------------------------------------------

    private DeliveryRecord persist(Long lineItem, Long io, Long brief,
                                   Long impressions, Long clicks, String spend,
                                   DeliveryStatus status) {
        DeliveryRecord r = DeliveryRecord.builder()
                .lineItemId(lineItem)
                .ioId(io)
                .campaignBriefId(brief)
                .reportingDate(LocalDate.of(2025, 1, 15))
                .deliveredImpressions(impressions)
                .clicks(clicks)
                .spend(new BigDecimal(spend))
                .source(Source.PublisherReport)
                .status(status)
                .build();
        return deliveryRepository.saveAndFlush(r);
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, actual.compareTo(new BigDecimal(expected)),
                () -> "expected " + expected + " but was " + actual);
    }
}
