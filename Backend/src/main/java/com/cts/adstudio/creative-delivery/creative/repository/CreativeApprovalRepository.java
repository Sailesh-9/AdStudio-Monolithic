package com.cts.creative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.creative.entity.CreativeApproval;

public interface CreativeApprovalRepository extends JpaRepository<CreativeApproval, Long> {
}