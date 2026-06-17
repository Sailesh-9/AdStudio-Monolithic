package com.cts.adstudio.creative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.adstudio.creative.entity.*;

public interface AssetLineItemLinkRepository extends JpaRepository<AssetLineItemLink, Long> {

    boolean existsByAssetAndLineItemId(CreativeAsset asset, Long lineItemId);
}