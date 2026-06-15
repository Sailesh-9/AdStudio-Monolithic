package com.cts.adstudio.iam.service;

import com.cts.adstudio.iam.dto.request.AuditLogRequest;
import com.cts.adstudio.iam.dto.response.AuditLogResponse;

import java.util.List;

public interface AuditLogService {

    /** Internal helper used by other services to record an action. */
    void record(Long userId, String action, String entityType);

    AuditLogResponse create(AuditLogRequest request);

    List<AuditLogResponse> getAll();

    AuditLogResponse getById(Long auditId);

    List<AuditLogResponse> getByUser(Long userId);
}
