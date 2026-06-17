package com.cts.adstudio.creative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.adstudio.creative.entity.CreativeApproval;

public interface CreativeApprovalRepository extends JpaRepository<CreativeApproval, Long> {
}