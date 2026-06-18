package com.cts.adstudio.mediaplan.service;

import com.cts.adstudio.mediaplan.dto.request.DeliveryRecordRequest;
import com.cts.adstudio.mediaplan.dto.response.DeliveryRecordResponse;
import java.util.List;

public interface DeliveryRecordService {
    DeliveryRecordResponse recordDelivery(DeliveryRecordRequest request);
    List<DeliveryRecordResponse> getDeliveryByLineItem(Integer lineItemId);
}