package com.cts.adstudio.notification.controller;

import com.cts.adstudio.notification.dto.request.NotificationRequest;
import com.cts.adstudio.notification.dto.response.NotificationResponse;
import com.cts.adstudio.notification.service.NotificationService;
import com.cts.adstudio.notification.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Create a notification (other services call this)
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> create(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification created", response));
    }

    // A user's inbox:  /api/notifications?userId=1&status=Unread
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @RequestParam Integer userId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notifications fetched", notificationService.getNotifications(userId, status)));
    }

    // Mark one as read
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notification marked as read", notificationService.markAsRead(id)));
    }

    // Unread count (for a UI badge): /api/notifications/unread-count?userId=1
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount(@RequestParam Integer userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(
                "Unread count fetched", Map.of("unreadCount", count)));
    }
}