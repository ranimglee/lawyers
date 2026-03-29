package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.AdminNotification;
import com.onat.jurist.lawyer.repository.AdminNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final AdminNotificationRepository repo;

    public List<AdminNotification> getAllNotifications() {
        return repo.findAll();
    }

    public void markAsSeen(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setSeen(true);
            repo.save(n);
        });
    }
}
