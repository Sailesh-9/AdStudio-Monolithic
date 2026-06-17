package com.cts.adstudio.advertiser.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.adstudio.advertiser.entity.TargetAudience;

@Repository
public interface TargetAudienceRepository extends JpaRepository<TargetAudience, Integer> {

    List<TargetAudience> findByBriefId(Integer briefId);

}