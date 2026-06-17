package com.cts.adstudio.advertiser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.adstudio.advertiser.entity.Advertiser;

@Repository
public interface AdvertiserRepository extends JpaRepository<Advertiser, Integer> { }