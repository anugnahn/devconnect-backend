package com.devconnect.devconnectbackend.controller;

import model.User;
import com.devconnect.devconnectbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getAllNotifications(user.getId()));
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnread(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(user.getId()));
    }

    @PostMapping("/read")
    public ResponseEntity<?> markRead(@AuthenticationPrincipal User user) {
        notificationService.markAllRead(user.getId());
        return ResponseEntity.ok().build();
    }
}