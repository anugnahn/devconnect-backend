package com.devconnect.devconnectbackend.service;

import model.Notification;
import model.User;
import com.devconnect.devconnectbackend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendJobMatchNotification(User developer, String jobTitle, String company, int matchScore) {
        // Save to DB
        Notification notification = Notification.builder()
                .user(developer)
                .title("New job match!")
                .message(jobTitle + " at " + company + " — " + matchScore + "% match")
                .type("JOB_MATCH")
                .build();
        notificationRepository.save(notification);

        // Send via WebSocket
        Map<String, Object> payload = Map.of(
                "type", "JOB_MATCH",
                "title", "New job match!",
                "message", jobTitle + " at " + company,
                "matchScore", matchScore,
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSendToUser(
                developer.getEmail(),
                "/queue/notifications",
                payload
        );
    }

    public List<Notification> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getAllNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAllRead(UUID userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }
}