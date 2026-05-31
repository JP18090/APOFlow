package com.apoflow.backend.service;

import com.apoflow.backend.api.dto.NotificationResponse;
import com.apoflow.backend.domain.AppNotification;
import com.apoflow.backend.domain.AppUser;
import com.apoflow.backend.domain.Role;
import com.apoflow.backend.repository.AppNotificationRepository;
import com.apoflow.backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final AppNotificationRepository notificationRepository;
    private final AppUserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(AppNotificationRepository notificationRepository,
                                AppUserRepository userRepository,
                                EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public List<NotificationResponse> findByRecipient(String recipient) {
        return notificationRepository.findByDestinatarioIn(List.of(recipient, "all")).stream()
                .map(this::map)
                .toList();
    }

    public void create(String id, String title, String time, boolean read, String recipient) {
        notificationRepository.save(new AppNotification(id, title, time, read, recipient));
        sendEmailToRecipient(recipient, title);
    }

    public void markAllAsRead(String recipient) {
        notificationRepository.findByDestinatarioIn(List.of(recipient, "all")).stream()
                .filter(notification -> !notification.isLida())
                .forEach(notification -> {
                    notification.setLida(true);
                    notificationRepository.save(notification);
                });
    }

    private void sendEmailToRecipient(String recipient, String title) {
        try {
            Role role = Role.valueOf(recipient.toUpperCase());
            userRepository.findFirstByPapel(role).ifPresent(user ->
                    emailService.sendApoNotification(user.getEmail(), user.getNome(), title, title));
        } catch (IllegalArgumentException ignored) {
            // recipient is a user id, not a role
            Optional<AppUser> userOpt = userRepository.findById(recipient);
            userOpt.ifPresent(user ->
                    emailService.sendApoNotification(user.getEmail(), user.getNome(), title, title));
        }
    }

    private NotificationResponse map(AppNotification notification) {
        return new NotificationResponse(notification.getId(), notification.getTitulo(), notification.getTempo(), notification.isLida());
    }
}
