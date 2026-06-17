package com.cts.adstudio.advertiser.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.adstudio.advertiser.entity.CampaignBrief;

@Repository
public interface CampaignBriefRepository extends JpaRepository<CampaignBrief, Integer> { 

    List<CampaignBrief> findByBrandId(Integer brandId);

}
