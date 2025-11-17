package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.entity.EmailNotification;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import com.onat.jurist.lawyer.repository.EmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AcceptanceService {

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
        throw new IllegalStateException("You are not the current assignee of this affaire");
    }

    affaire.setStatut(StatutAffaire.ACCEPTEE);
    affaireRepository.save(affaire);


    avocat.setAffairesEnCours(avocat.getAffairesEnCours() + 1);
    avocat.setAffairesAcceptees(avocat.getAffairesAcceptees() + 1);
    avocatRepository.save(avocat);

    List<EmailNotification> emails = emailRepo.findAll();
    emails.stream()
            .filter(e -> e.getAffaire() != null && e.getAffaire().getId().equals(affaireId))
            .forEach(e -> { e.setAccepted(true); emailRepo.save(e); });
}


@Transactional
public void refuseAffaire(Long affaireId, Long avocatId) {
    Affaire affaire = affaireRepository.findById(affaireId)
            .orElseThrow(() -> new IllegalArgumentException("Affaire not found"));

    if (affaire.getAvocatAssigne() == null || !affaire.getAvocatAssigne().getId().equals(avocatId)) {
        throw new IllegalStateException("You are not the current assignee of this affaire");
    }

    List<EmailNotification> emails = emailRepo.findAll();
    emails.stream()
            .filter(e -> e.getAffaire() != null && e.getAffaire().getId().equals(affaireId))
            .forEach(e -> { e.setAccepted(false); emailRepo.save(e); });

    affaire.setAvocatAssigne(null);
    affaireRepository.save(affaire);


    assignmentService.assignBestLawyer(affaire);
    }

    /**
     * Vérifie que le token correspond à l'affaire et n'est pas expiré
     */
    public boolean verifyToken(Long affaireId, String token) {
        Optional<EmailNotification> emailOpt = emailRepo.findByActionToken(token);

        if (emailOpt.isEmpty()) return false;

        EmailNotification email = emailOpt.get();

        // Vérifier que le token correspond à l'affaire
        if (!email.getAffaire().getId().equals(affaireId)) return false;

        // Vérifier que le token n'est pas expiré
        return email.getTokenExpiry().isAfter(LocalDateTime.now());
    }

    /**
     * Récupère l'ID de l'avocat à partir du token
     */
    public Long getAvocatIdFromToken(String token) {
        Optional<EmailNotification> emailOpt = emailRepo.findByActionToken(token);
        return emailOpt.map(e -> e.getAffaire().getAvocatAssigne().getId()).orElse(null);
    }
}