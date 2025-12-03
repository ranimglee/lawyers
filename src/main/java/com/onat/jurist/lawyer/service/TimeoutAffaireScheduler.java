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
    public void processTimeoutAffaires() {
        LocalDateTime now = LocalDateTime.now();

        List<Affaire> pendingAffaires = affaireRepository.findAllByStatut(StatutAffaire.EN_ATTENTE);

        for (Affaire affaire : pendingAffaires) {
            Avocat assignedLawyer = affaire.getAvocatAssigne();
            LocalDateTime assignmentTime = assignedLawyer != null ? assignedLawyer.getLastAssignedAt() : null;

            if (assignedLawyer == null || assignmentTime == null) {
                continue;
            }

            // Determine timeout based on affair type
            long timeoutMinutes = switch (affaire.getType()) {
                case ENQUETE, ENQUETEUR_PRELIMINAIRE -> 15;
                default -> 7 * 24 * 60;
            };

            LocalDateTime expiry = assignmentTime.plusMinutes(timeoutMinutes);

            if (now.isAfter(expiry)) {
                assignedLawyer.setAffairesRefusees(assignedLawyer.getAffairesRefusees() + 1);
                avocatRepository.save(assignedLawyer);

                affaire.setAvocatAssigne(null);
                affaireRepository.save(affaire);

                log.info("⌛ Affaire {} timed out for lawyer {}. Lawyer refusal recorded.", affaire.getId(), assignedLawyer.getId());

               assignmentService.assignBestLawyer(affaire);
                log.info("🔄 Affaire {} reassigned automatically after timeout.", affaire.getId());
            }
        }
    }

}
