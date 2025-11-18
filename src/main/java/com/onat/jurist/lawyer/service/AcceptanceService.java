package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.entity.EmailNotification;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import com.onat.jurist.lawyer.repository.EmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AcceptanceService {

    private static final Logger log = LoggerFactory.getLogger(AcceptanceService.class);

    private final AffaireRepository affaireRepository;
    private final AvocatRepository avocatRepository;
    private final EmailNotificationRepository emailRepo;
    private final AffaireAssignmentService assignmentService;

    public void acceptAffaire(Long affaireId, Long avocatId) {
        Affaire affaire = affaireRepository.findById(affaireId)
                .orElseThrow(() -> new IllegalArgumentException("Affaire not found"));
        Avocat avocat = avocatRepository.findById(avocatId)
                .orElseThrow(() -> new IllegalArgumentException("Avocat not found"));

        if (affaire.getAvocatAssigne() == null || !affaire.getAvocatAssigne().getId().equals(avocatId)) {
            log.warn("⚠️ Lawyer {} tried to accept affaire {} but is not the assignee", avocatId, affaireId);
            throw new IllegalStateException("You are not the current assignee of this affaire");
        }

        affaire.setStatut(StatutAffaire.ACCEPTEE);
        affaireRepository.save(affaire);
        log.info("✅ Affaire {} accepted by lawyer {} ({})", affaireId, avocat.getPrenom(), avocat.getNom());

        avocat.setAffairesEnCours(avocat.getAffairesEnCours() + 1);
        avocat.setAffairesAcceptees(avocat.getAffairesAcceptees() + 1);
        avocatRepository.save(avocat);
        log.info("📈 Lawyer {} now has {} ongoing affaires and {} accepted affaires", avocatId,
                avocat.getAffairesEnCours(), avocat.getAffairesAcceptees());

        List<EmailNotification> emails = emailRepo.findAll();
        emails.stream()
                .filter(e -> e.getAffaire() != null && e.getAffaire().getId().equals(affaireId))
                .forEach(e -> {
                    e.setAccepted(true);
                    emailRepo.save(e);
                    log.info("✉️ Notification {} marked as accepted for affaire {}", e.getId(), affaireId);
                });
    }

    @Transactional
    public void refuseAffaire(Long affaireId, Long avocatId) {
        Affaire affaire = affaireRepository.findById(affaireId)
                .orElseThrow(() -> new IllegalArgumentException("Affaire not found"));
        Avocat avocat = avocatRepository.findById(avocatId)
                .orElseThrow(() -> new IllegalArgumentException("Avocat not found"));

        if (affaire.getAvocatAssigne() == null || !affaire.getAvocatAssigne().getId().equals(avocatId)) {
            throw new IllegalStateException("You are not the current assignee of this affaire");
        }

        // Mark refusal in notifications and store the lawyer
        List<EmailNotification> emails = Optional.ofNullable(affaire.getNotifications()).orElse(List.of());
        emails.stream()
                .filter(e -> e.getAffaire() != null && e.getAffaire().getId().equals(affaireId))
                .forEach(e -> {
                    e.setAccepted(false);
                    e.setAvocat(avocat); // save who refused
                    emailRepo.save(e);
                    log.info("✉️ Notification {} marked as refused for affaire {}", e.getId(), affaireId);
                });

        // Clear the assigned lawyer
        affaire.setAvocatAssigne(null);
        avocat.setAffairesRefusees(avocat.getAffairesRefusees() + 1);
        affaireRepository.save(affaire);
        log.info("❌ Lawyer {} refused affaire {}. Total refused affaires: {}", avocatId, affaireId, avocat.getAffairesRefusees());

        // Assign next best lawyer
        assignmentService.assignBestLawyer(affaire);
    }

    /**
     * Vérifie que le token correspond à l'affaire et n'est pas expiré
     */
    public boolean verifyToken(Long affaireId, String token) {
        Optional<EmailNotification> emailOpt = emailRepo.findByActionToken(token);
        if (emailOpt.isEmpty()) {
            log.warn("❌ Token {} not found for affaire {}", token, affaireId);
            return false;
        }

        EmailNotification email = emailOpt.get();

        if (!email.getAffaire().getId().equals(affaireId)) {
            log.warn("❌ Token {} does not match affaire {}", token, affaireId);
            return false;
        }

        boolean valid = email.getTokenExpiry().isAfter(LocalDateTime.now());
        log.info(valid ? "✅ Token {} verified for affaire {}" : "❌ Token {} expired for affaire {}", token, affaireId, token);
        return valid;
    }

    public Long getAvocatIdFromToken(String token) {
        Optional<EmailNotification> emailOpt = emailRepo.findByActionToken(token);
        Long avocatId = emailOpt.map(e -> e.getAffaire().getAvocatAssigne().getId()).orElse(null);

        if (avocatId != null) {
            log.info("🔑 Token {} corresponds to lawyer {}", token, avocatId);
        } else {
            log.warn("❌ Token {} has no assigned lawyer", token);
        }

        return avocatId;
    }

}
