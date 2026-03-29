package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
}