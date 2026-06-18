package com.cts.adstudio.mediaplan.service;

import com.cts.adstudio.mediaplan.dto.request.MediaLineItemRequest;
import com.cts.adstudio.mediaplan.dto.response.MediaLineItemResponse;
import java.util.List;

public interface MediaLineItemService {
    MediaLineItemResponse createLineItem(Integer planId, MediaLineItemRequest request);
    List<MediaLineItemResponse> getLineItemsByPlan(Integer planId);
    MediaLineItemResponse getLineItemById(Integer lineItemId);
    MediaLineItemResponse updateLineItem(Integer lineItemId, MediaLineItemRequest request);
    MediaLineItemResponse updateLineItemStatus(Integer lineItemId, String status);
    void deleteLineItem(Integer lineItemId);
}