package com.cts.adstudio.iam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Payload for manually recording an audit log entry. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "action is required")
    private String action;

    @NotBlank(message = "entityType is required")
    private String entityType;  // table name
}
