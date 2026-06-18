package com.cts.adstudio.mediaplan.repository;

import com.cts.adstudio.mediaplan.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaplanAuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Integer entityId);
    List<AuditLog> findByUserId(Integer userId);
}