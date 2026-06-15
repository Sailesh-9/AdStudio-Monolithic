package com.cts.adstudio.iam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Audit trail entry. Records who did what, to which entity type, and when.
 * userId is stored as a plain value (audit records are immutable snapshots
 * and should not depend on the live user row).
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** e.g. "REGISTER_USER", "LOGIN", "APPROVE_CREATIVE". */
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    /** The entity affected, e.g. "User", "ClientInvoice". */
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
