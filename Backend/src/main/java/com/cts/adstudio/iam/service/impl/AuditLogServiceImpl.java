package com.cts.adstudio.iam.service.impl;

import com.cts.adstudio.iam.dto.request.AuditLogRequest;
import com.cts.adstudio.iam.dto.response.AuditLogResponse;
import com.cts.adstudio.iam.entity.AuditLog;
import com.cts.adstudio.iam.exception.ResourceNotFoundException;
import com.cts.adstudio.iam.repository.AuditLogRepository;
import com.cts.adstudio.iam.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void record(Long userId, String action, String entityType) {
        AuditLog entry = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(entry);
        log.debug("Audit recorded userId={} action={} entityType={}", userId, action, entityType);
    }

    @Override
    @Transactional
    public AuditLogResponse create(AuditLogRequest request) {
        AuditLog entry = AuditLog.builder()
                .userId(request.getUserId())
                .action(request.getAction())
                .entityType(request.getEntityType())
                .timestamp(LocalDateTime.now())
                .build();
        return toResponse(auditLogRepository.save(entry));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getById(Long auditId) {
        AuditLog entry = auditLogRepository.findById(auditId)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found with id: " + auditId));
        return toResponse(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getByUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    private AuditLogResponse toResponse(AuditLog entry) {
        return AuditLogResponse.builder()
                .auditId(entry.getAuditId())
                .userId(entry.getUserId())
                .action(entry.getAction())
                .entityType(entry.getEntityType())
                .timestamp(entry.getTimestamp())
                .build();
    }
}
