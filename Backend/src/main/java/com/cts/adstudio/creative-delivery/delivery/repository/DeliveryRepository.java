package com.cts.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.delivery.entity.DeliveryRecord;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<DeliveryRecord, Long> {

    List<DeliveryRecord> findByLineItemId(Long lineItemId);
}