package com.apoflow.backend.repository;

import com.apoflow.backend.domain.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppNotificationRepository extends JpaRepository<AppNotification, String> {
    List<AppNotification> findByDestinatarioIn(List<String> destinatarios);
}
