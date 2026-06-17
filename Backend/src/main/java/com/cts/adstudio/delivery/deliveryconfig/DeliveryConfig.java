package com.cts.adstudio.delivery.deliveryconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Externalised, tunable rules for the Delivery module's pacing engine
 * (Maintainability NFR: "configurable ... billing cycle rules"). Bound from
 * {@code adstudio.delivery.pacing.*} in {@code application.properties}; the
 * defaults below apply when nothing is configured.
 *
 * <pre>
 * adstudio.delivery.pacing.under-delivery-threshold=80
 * adstudio.delivery.pacing.over-delivery-threshold=110
 * adstudio.delivery.pacing.flight-end-warning-days=3
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "adstudio.delivery.pacing")
@Data
public class DeliveryConfig {

    /** Below this delivered-vs-expected percentage an UnderDelivery alert is raised. */
    private BigDecimal underDeliveryThreshold = new BigDecimal("80");

    /** Above this delivered-vs-expected percentage an OverDelivery alert is raised. */
    private BigDecimal overDeliveryThreshold = new BigDecimal("110");

    /** Raise a FlightEndApproaching alert when the flight ends within this many days. */
    private long flightEndWarningDays = 3L;
}
