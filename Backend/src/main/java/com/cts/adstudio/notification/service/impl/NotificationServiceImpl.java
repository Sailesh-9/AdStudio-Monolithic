package com.cts.adstudio.notification.service.impl;

import com.cts.adstudio.notification.dto.request.NotificationRequest;
import com.cts.adstudio.notification.dto.response.NotificationResponse;
import com.cts.adstudio.notification.entity.Notification;
import com.cts.adstudio.notification.exception.ResourceNotFoundException;
import com.cts.adstudio.notification.repository.NotificationRepository;
import com.cts.adstudio.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification n = Notification.builder()
                .userId(request.getUserId())
                .message(request.getMessage())
                .category(request.getCategory())
                .status(Notification.NotificationStatus.Unread)
                .build();
        Notification saved = notificationRepository.save(n);
        log.info("Notification {} created for user {}", saved.getNotificationId(), request.getUserId());
        return mapToResponse(saved);
    }

    @Override
    public List<NotificationResponse> getNotifications(Integer userId, String status) {
        List<Notification> list;
        if (status == null || status.isBlank()) {
            list = notificationRepository.findByUserId(userId);
        } else {
            Notification.NotificationStatus s;
            try {
                s = Notification.NotificationStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status + ". Allowed: Unread, Read");
            }
            list = notificationRepository.findByUserIdAndStatus(userId, s);
        }
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(Integer notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with ID: " + notificationId));
        n.setStatus(Notification.NotificationStatus.Read);
        return mapToResponse(notificationRepository.save(n));
    }

    @Override
    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUserIdAndStatus(
                userId, Notification.NotificationStatus.Unread);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .userId(n.getUserId())
                .message(n.getMessage())
                .category(n.getCategory())
                .status(n.getStatus() != null ? n.getStatus().name() : null)
                .createdDate(n.getCreatedDate())
                .build();
    }
}