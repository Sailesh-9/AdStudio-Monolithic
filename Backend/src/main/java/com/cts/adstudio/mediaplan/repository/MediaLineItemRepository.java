package com.cts.adstudio.mediaplan.repository;

import com.cts.adstudio.mediaplan.entity.MediaLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaLineItemRepository extends JpaRepository<MediaLineItem, Integer> {

    List<MediaLineItem> findByMediaPlan_PlanId(Integer planId);
    List<MediaLineItem> findByStatus(MediaLineItem.LineItemStatus status);
    List<MediaLineItem> findByChannel(MediaLineItem.Channel channel);
}