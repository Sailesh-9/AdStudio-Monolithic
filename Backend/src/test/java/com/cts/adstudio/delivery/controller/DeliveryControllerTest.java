package com.cts.adstudio.delivery.controller;

import com.cts.adstudio.delivery.deliveryexception.DeliveryExceptionHandler;
import com.cts.adstudio.delivery.deliveryexception.DeliveryNotFoundException;
import com.cts.adstudio.delivery.dto.DeliveryRecordResponse;
import com.cts.adstudio.delivery.dto.DeliverySummaryResponse;
import com.cts.adstudio.delivery.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Web-layer tests for {@link DeliveryController} using standalone MockMvc with a
 * mocked service. This verifies routing, request validation, status codes, the
 * envelope shape, and 404 translation via {@link DeliveryExceptionHandler} &mdash;
 * without booting Spring, security, or a database. The two finance-facing paths
 * ({@code delivered-spend} / {@code delivered-value}) are covered explicitly,
 * since other modules depend on those exact URLs and the numeric {@code data}.
 */
@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {

    @Mock
    private DeliveryService deliveryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new DeliveryController(deliveryService))
                .setControllerAdvice(new DeliveryExceptionHandler())
                .build();
    }

    private DeliveryRecordResponse sampleRecord() {
        return DeliveryRecordResponse.builder()
                .deliveryId(1L)
                .lineItemId(10L)
                .ioId(20L)
                .campaignBriefId(30L)
                .reportingDate(LocalDate.of(2025, 1, 15))
                .deliveredImpressions(1000L)
                .clicks(50L)
                .spend(new BigDecimal("123.45"))
                .ctr(new BigDecimal("5.00"))
                .source("PublisherReport")
                .status("Accepted")
                .build();
    }

    @Test
    void recordDelivery_validBody_returns201AndData() throws Exception {
        when(deliveryService.recordDelivery(any())).thenReturn(sampleRecord());

        String body = "{"
                + "\"lineItemId\":10,"
                + "\"ioId\":20,"
                + "\"campaignBriefId\":30,"
                + "\"reportingDate\":\"2025-01-15\","
                + "\"deliveredImpressions\":1000,"
                + "\"clicks\":50,"
                + "\"spend\":123.45,"
                + "\"source\":\"PublisherReport\","
                + "\"status\":\"Accepted\"}";

        mockMvc.perform(post("/api/delivery/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deliveryId").value(1))
                .andExpect(jsonPath("$.data.status").value("Accepted"));
    }

    @Test
    void recordDelivery_missingRequiredField_returns400() throws Exception {
        // lineItemId omitted -> @NotNull violation -> handled as 400
        String body = "{"
                + "\"reportingDate\":\"2025-01-15\","
                + "\"deliveredImpressions\":1000,"
                + "\"clicks\":50,"
                + "\"spend\":123.45}";

        mockMvc.perform(post("/api/delivery/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(deliveryService, never()).recordDelivery(any());
    }

    @Test
    void getById_found_returns200() throws Exception {
        when(deliveryService.getById(1L)).thenReturn(sampleRecord());

        mockMvc.perform(get("/api/delivery/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deliveryId").value(1));
    }

    @Test
    void getById_missing_returns404() throws Exception {
        when(deliveryService.getById(99L))
                .thenThrow(new DeliveryNotFoundException("Delivery record not found with ID: 99"));

        mockMvc.perform(get("/api/delivery/records/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deliveredSpendForCampaign_returns200WithNumericData() throws Exception {
        when(deliveryService.deliveredSpendForCampaign(30L)).thenReturn(new BigDecimal("500.00"));

        mockMvc.perform(get("/api/delivery/campaigns/30/delivered-spend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(500.0));
    }

    @Test
    void deliveredValueForInsertionOrder_returns200WithNumericData() throws Exception {
        when(deliveryService.deliveredValueForInsertionOrder(20L)).thenReturn(new BigDecimal("250.00"));

        mockMvc.perform(get("/api/delivery/insertion-orders/20/delivered-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(250.0));
    }

    @Test
    void getByStatus_returns200WithList() throws Exception {
        when(deliveryService.getByStatus("Accepted")).thenReturn(List.of(sampleRecord()));

        mockMvc.perform(get("/api/delivery/records").param("status", "Accepted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("Accepted"));
    }

    @Test
    void updateStatus_returns200() throws Exception {
        DeliveryRecordResponse disputed = sampleRecord();
        disputed.setStatus("Disputed");
        when(deliveryService.updateStatus(1L, "Disputed")).thenReturn(disputed);

        mockMvc.perform(put("/api/delivery/records/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Disputed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("Disputed"));
    }

    @Test
    void summaryForLineItem_returns200() throws Exception {
        DeliverySummaryResponse summary = DeliverySummaryResponse.builder()
                .scope("LineItem")
                .scopeId(10L)
                .recordCount(2L)
                .totalDeliveredImpressions(4000L)
                .totalClicks(30L)
                .totalSpend(new BigDecimal("300.50"))
                .ctr(new BigDecimal("0.75"))
                .build();
        when(deliveryService.summaryForLineItem(10L)).thenReturn(summary);

        mockMvc.perform(get("/api/delivery/line-items/10/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.scope").value("LineItem"))
                .andExpect(jsonPath("$.data.totalDeliveredImpressions").value(4000));
    }
}
