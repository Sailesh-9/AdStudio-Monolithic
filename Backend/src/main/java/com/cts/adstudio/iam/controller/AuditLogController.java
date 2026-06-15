package com.cts.adstudio.iam.controller;

import com.cts.adstudio.iam.dto.request.AuditLogRequest;
import com.cts.adstudio.iam.dto.response.AuditLogResponse;
import com.cts.adstudio.iam.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Story: Audit Log API. Restricted to ADMIN (RBAC via @PreAuthorize).
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Log", description = "Track and query system actions (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "Record an action in the audit log")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AuditLogResponse> create(@Valid @RequestBody AuditLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditLogService.create(request));
    }

    @Operation(summary = "Get all audit log entries")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAll() {
        return ResponseEntity.ok(auditLogService.getAll());
    }

    @Operation(summary = "Get an audit log entry by id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{auditId}")
    public ResponseEntity<AuditLogResponse> getById(@PathVariable Long auditId) {
        return ResponseEntity.ok(auditLogService.getById(auditId));
    }

    @Operation(summary = "Get audit log entries for a specific user")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getByUser(userId));
    }
}
