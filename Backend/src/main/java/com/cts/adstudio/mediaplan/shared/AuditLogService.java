package com.cts.adstudio.mediaplan.shared;

import com.cts.adstudio.mediaplan.entity.AuditLog;
import com.cts.adstudio.mediaplan.repository.MediaplanAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final MediaplanAuditLogRepository auditLogRepository;

    /**
     * Records an action in the audit log.
     * @param userId     who performed the action
     * @param action     what happened (e.g. "IO_STATUS_CHANGED_TO_Confirmed")
     * @param entityType the entity type (e.g. "InsertionOrder")
     * @param entityId   the ID of the affected record
     */
    public void log(Integer userId, String action, String entityType, Integer entityId) {
        AuditLog entry = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .build();
        auditLogRepository.save(entry);
        log.info("AUDIT: user={} action={} {}#{}", userId, action, entityType, entityId);
    }
}