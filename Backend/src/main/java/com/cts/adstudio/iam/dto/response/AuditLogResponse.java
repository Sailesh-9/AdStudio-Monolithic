package com.cts.adstudio.iam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** View of a single audit log entry. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long auditId;
    private Long userId;
    private String action;
    private String entityType;
    private LocalDateTime timestamp;
}
