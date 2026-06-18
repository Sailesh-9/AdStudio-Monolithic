package com.cts.adstudio.mediaplan.repository;

import com.cts.adstudio.mediaplan.entity.InsertionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsertionOrderRepository extends JpaRepository<InsertionOrder, Integer> {

    List<InsertionOrder> findByMediaLineItem_LineItemId(Integer lineItemId);
    List<InsertionOrder> findByPublisherId(Integer publisherId);
    List<InsertionOrder> findByStatus(InsertionOrder.IOStatus status);
}