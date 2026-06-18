package com.cts.adstudio.mediaplan.service;

import com.cts.adstudio.mediaplan.dto.request.InsertionOrderRequest;
import com.cts.adstudio.mediaplan.dto.response.InsertionOrderResponse;
import java.util.List;

public interface InsertionOrderService {
    InsertionOrderResponse createInsertionOrder(InsertionOrderRequest request);
    InsertionOrderResponse getInsertionOrderById(Integer ioId);
    List<InsertionOrderResponse> getAllInsertionOrders();
    List<InsertionOrderResponse> getInsertionOrdersByPublisher(Integer publisherId);
    InsertionOrderResponse updateStatus(Integer ioId, String status);
}