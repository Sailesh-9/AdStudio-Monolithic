package com.cts.adstudio.mediaplan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Monolith note: IAM also defines an "AuditLog" entity mapped to "audit_log".
// To coexist in a single persistence unit, this one uses a distinct JPA entity
// name and table. No JPQL references this entity by name, so this is transparent
// to the rest of the mediaplan module (the Java class name stays "AuditLog").
@Entity(name = "MediaplanAuditLog")
@Table(name = "mediaplan_audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Integer auditId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}