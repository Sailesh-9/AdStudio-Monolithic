package com.cts.adstudio.mediaplan.repository;

import com.cts.adstudio.mediaplan.entity.MediaPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaPlanRepository extends JpaRepository<MediaPlan, Integer> {

    List<MediaPlan> findByBriefId(Integer briefId);
    List<MediaPlan> findByPlannerId(Integer plannerId);
    List<MediaPlan> findByStatus(MediaPlan.MediaPlanStatus status);
}