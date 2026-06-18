package com.cts.adstudio.notification.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Integer notificationId;
    private Integer userId;
    private String message;
    private String category;
    private String status;
    private LocalDateTime createdDate;
}