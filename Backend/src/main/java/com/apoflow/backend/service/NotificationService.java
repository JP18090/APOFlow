package com.apoflow.backend.service;

import com.apoflow.backend.api.dto.NotificationResponse;
import com.apoflow.backend.domain.AppNotification;
import com.apoflow.backend.repository.AppNotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final AppNotificationRepository notificationRepository;

    public NotificationService(AppNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponse> findByRecipient(String recipient) {
        return notificationRepository.findByDestinatarioIn(List.of(recipient, "all")).stream()
                .map(this::map)
                .toList();
    }

    public void create(String id, String title, String time, boolean read, String recipient) {
        notificationRepository.save(new AppNotification(id, title, time, read, recipient));
    }

    private NotificationResponse map(AppNotification notification) {
        return new NotificationResponse(notification.getId(), notification.getTitulo(), notification.getTempo(), notification.isLida());
    }
}
