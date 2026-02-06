package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.entity.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Long> {
    Optional<EmailNotification> findByActionToken(String token);

    Optional<EmailNotification> findByAffaireAndAvocat(Affaire affaire, Avocat avocat);


}