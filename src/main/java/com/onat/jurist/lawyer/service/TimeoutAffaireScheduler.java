package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeoutAffaireScheduler {

    private static final Logger log = LoggerFactory.getLogger(TimeoutAffaireScheduler.class);

    private final AffaireRepository affaireRepository;
    private final AvocatRepository avocatRepository;
    private final AffaireAssignmentService assignmentService;

    @Scheduled(fixedRate = 60000)
    @Transactional

    public void processTimeoutAffaires() {
        LocalDateTime now = LocalDateTime.now();
        log.info("🔄 Scheduler started at {}", now);

        // Récupérer toutes les affaires en attente
        List<Affaire> pendingAffaires = affaireRepository.findAllByStatutWithNotifications(StatutAffaire.EN_ATTENTE);
        log.info("📄 {} affaires en attente récupérées.", pendingAffaires.size());

        for (Affaire affaire : pendingAffaires) {
            log.info("➡ Vérification de l'affaire {} (type {})", affaire.getId(), affaire.getType());

            Avocat assignedLawyer = affaire.getAvocatAssigne();
            LocalDateTime assignmentTime = affaire.getAssignedAt(); // Utiliser assignedAt spécifique à l'affaire

            if (assignedLawyer == null) {
                log.info("   ❌ Pas d'avocat assigné pour l'affaire {}.", affaire.getId());
                continue;
            }
            if (assignmentTime == null) {
                log.info("   ❌ Aucun timestamp d'assignation pour l'affaire {}.", affaire.getId());
                continue;
            }

            log.info("   ⏱ Affaire assignée à l'avocat {} depuis {}", assignedLawyer.getId(), assignmentTime);

            // Déterminer le timeout selon le type d’affaire
            long timeoutMinutes = switch (affaire.getType()) {
                case ENQUETE, ENQUETEUR_PRELIMINAIRE -> 5;
                default -> 7 * 24 * 60; // 7 jours
            };
            LocalDateTime expiry = assignmentTime.plusMinutes(timeoutMinutes);
            log.info("   ⏳ Timeout prévu à {}", expiry);

            if (now.isAfter(expiry)) {
                log.info("   ⚠ Affaire {} a dépassé le délai d'assignation.", affaire.getId());

                // Incrémenter le nombre de refus de l'avocat
                assignedLawyer.setAffairesRefusees(assignedLawyer.getAffairesRefusees() + 1);
                avocatRepository.save(assignedLawyer);
                log.info("   ✅ Avocat {}: nombre de refus mis à jour à {}", assignedLawyer.getId(), assignedLawyer.getAffairesRefusees());

                // Libérer l'affaire pour réassignation
                affaire.setAvocatAssigne(null);
                affaire.setAssignedAt(null);
                affaireRepository.save(affaire);
                log.info("   ✅ Affaire {} libérée pour réassignation.", affaire.getId());

                // Réassignation automatique
                assignmentService.assignBestLawyer(affaire);
                log.info("   🔄 Affaire {} réassignée automatiquement.", affaire.getId());
            } else {
                log.info("   ✅ Affaire {} toujours dans le délai.", affaire.getId());
            }
        }

        log.info("🔚 Scheduler terminé à {}", LocalDateTime.now());
    }
}
