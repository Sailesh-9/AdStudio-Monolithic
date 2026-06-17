package com.cts.adstudio.delivery.controller;

import com.cts.adstudio.config.SecurityConfig;
import com.cts.adstudio.delivery.service.DeliveryService;
import com.cts.adstudio.iam.security.CustomUserDetailsService;
import com.cts.adstudio.iam.security.JwtAuthenticationFilter;
import com.cts.adstudio.iam.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security-slice tests for {@link DeliveryController}. Boots only the web layer plus
 * the real {@link SecurityConfig} and {@link JwtAuthenticationFilter}, so the
 * authorization rules - the URL allowlist and the method-level {@code @PreAuthorize}
 * - are exercised without a database or a running server.
 *
 * <p>{@link JwtService} and {@link CustomUserDetailsService} are mocked only to
 * satisfy the security beans; with no {@code Authorization} header on these
 * requests, neither is actually invoked. {@code @WithMockUser} populates the
 * security context directly.</p>
 */
@WebMvcTest(DeliveryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class DeliveryControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService deliveryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void protectedEndpoint_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/delivery/records/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void openDeliveredSpendEndpoint_withoutToken_isOk() throws Exception {
        when(deliveryService.deliveredSpendForCampaign(5L)).thenReturn(BigDecimal.valueOf(1000));

        mockMvc.perform(get("/api/delivery/campaigns/5/delivered-spend"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DELIVERY_PUBLISHER")
    void protectedEndpoint_withPublisherRole_isOk() throws Exception {
        when(deliveryService.getByStatus("Accepted")).thenReturn(List.of());

        mockMvc.perform(get("/api/delivery/records").param("status", "Accepted"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BRAND_ADVERTISER")
    void protectedEndpoint_withWrongRole_isForbidden() throws Exception {
        mockMvc.perform(get("/api/delivery/records/1"))
                .andExpect(status().isForbidden());
    }
}
