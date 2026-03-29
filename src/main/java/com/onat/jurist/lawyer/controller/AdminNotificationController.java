package com.onat.jurist.lawyer.controller;

import com.onat.jurist.lawyer.entity.AdminNotification;
import com.onat.jurist.lawyer.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService service;

    @GetMapping
    public List<AdminNotification> getAllNotifications() {
        return service.getAllNotifications();
    }

    @PostMapping("/{id}/mark-seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable Long id) {
        service.markAsSeen(id);
        return ResponseEntity.ok().build();
    }
}