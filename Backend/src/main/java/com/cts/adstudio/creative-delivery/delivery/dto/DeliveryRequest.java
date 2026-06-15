package com.cts.delivery.dto;

public record DeliveryRequest(Long lineItemId, Integer targetImpressions, Integer deliveredImpressions) {
    
}