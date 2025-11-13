package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Long> {
    List<EmailNotification> findByAffaireId(Long affaireId);
}