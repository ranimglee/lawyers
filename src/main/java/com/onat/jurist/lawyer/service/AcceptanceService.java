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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AcceptanceService {

    private static final Logger log = LoggerFactory.getLogger(AcceptanceService.class);

    private final AffaireRepository affaireRepository;
    private final AvocatRepository avocatRepository;
    private final EmailNotificationRepository emailRepo;
    private final AffaireAssignmentService assignmentService;

    @Transactional
    public void acceptAffaire(Long affaireId, Long avocatId) {
        Affaire affaire = affaireRepository.findById(affaireId)
                .orElseThrow(() -> new IllegalArgumentException("Affaire not found"));
        Avocat avocat = avocatRepository.findById(avocatId)
                .orElseThrow(() -> new IllegalArgumentException("Avocat not found"));

        if (affaire.getAvocatAssigne() == null || !affaire.getAvocatAssigne().getId().equals(avocatId)) {
            throw new IllegalStateException("You are not the current assignee of this affaire");
        }

        // Check if the affaire has timed out
        if (hasAffaireTimedOut(affaire)) {
            throw new IllegalStateException("⏳ This affaire has timed out and can no longer be accepted.");
        }

        affaire.setStatut(StatutAffaire.ACCEPTEE);
        affaireRepository.save(affaire);

        avocat.setAffairesEnCours(avocat.getAffairesEnCours() + 1);
        avocat.setAffairesAcceptees(avocat.getAffairesAcceptees() + 1);
        avocatRepository.save(avocat);
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

        if (hasAffaireTimedOut(affaire)) {
            throw new IllegalStateException("⏳ This affaire has timed out and can no longer be refused.");
        }

        affaire.setAvocatAssigne(null);
        avocat.setAffairesRefusees(avocat.getAffairesRefusees() + 1);
        affaireRepository.save(affaire);
        avocatRepository.save(avocat);

        assignmentService.assignBestLawyer(affaire);
    }
    /**
     * Returns true if the assigned lawyer can no longer act on the affaire
     */
    private boolean hasAffaireTimedOut(Affaire affaire) {
        Avocat lawyer = affaire.getAvocatAssigne();
        if (lawyer == null || lawyer.getLastAssignedAt() == null) return false;

        long timeoutMinutes = switch (affaire.getType()) {
            case ENQUETE, ENQUETEUR_PRELIMINAIRE -> 15;
            default -> 7 * 24 * 60;
        };

        LocalDateTime expiry = lawyer.getLastAssignedAt().plusMinutes(timeoutMinutes);
        return LocalDateTime.now().isAfter(expiry);
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
