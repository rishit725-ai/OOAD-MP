package com.disaster.response.service;

import com.disaster.response.model.Notification;
import com.disaster.response.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SRP: NotificationService handles only notification retrieval and management.
 * Notification creation happens via NotificationObserver (Observer pattern).
 */
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> findAll() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Notification> findUnread() {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public long countUnread() {
        return notificationRepository.countByIsReadFalse();
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }
}
