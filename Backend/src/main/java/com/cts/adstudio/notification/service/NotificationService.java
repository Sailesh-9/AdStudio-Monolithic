package com.cts.adstudio.notification.service;

import com.cts.adstudio.notification.dto.request.NotificationRequest;
import com.cts.adstudio.notification.dto.response.NotificationResponse;
import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    List<NotificationResponse> getNotifications(Integer userId, String status);
    NotificationResponse markAsRead(Integer notificationId);
    long getUnreadCount(Integer userId);
}