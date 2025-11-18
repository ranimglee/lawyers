package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AffaireAssignmentService {
    private static final Logger log = LoggerFactory.getLogger(AffaireAssignmentService.class);

    private final AvocatRepository avocatRepository;
    private final EmailNotificationService emailService;
    private final AffaireRepository affaireRepository;

    @Transactional
    public Optional<Avocat> assignBestLawyer(Affaire affaire) {
        log.info("📝 Starting assignment for affaire '{}'", affaire.getTitre());

        // Collect lawyers who already refused this affaire
        List<Long> refusedIds = Optional.ofNullable(affaire.getNotifications())
                .orElse(List.of())
                .stream()
                .filter(e -> Boolean.FALSE.equals(e.isAccepted()) && e.getAvocat() != null)
                .map(e -> e.getAvocat().getId())
                .toList();

        log.info("🚫 Lawyers who refused affaire '{}': {}", affaire.getTitre(), refusedIds);

        List<Avocat> list = avocatRepository.findAll();

        List<Avocat> sorted = list.stream()
                .filter(a -> !refusedIds.contains(a.getId())) // skip refused
                .sorted(Comparator
                        .comparingInt(Avocat::getAffairesEnCours)
                        .thenComparing(a -> hasThisMonthAssignment(a) ? 1 : 0)
                        .thenComparing(Avocat::getDateInscription, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(a -> a.getLastAssignedAt() == null ? LocalDateTime.MIN : a.getLastAssignedAt())
                )
                .toList();

        if (sorted.isEmpty()) {
            log.warn("⚠️ No available lawyers for affaire '{}'", affaire.getTitre());
            return Optional.empty();
        }

        Avocat chosen = sorted.get(0);
        affaire.setAvocatAssigne(chosen);
        affaireRepository.save(affaire);

        chosen.setLastAssignedAt(LocalDateTime.now());
        avocatRepository.save(chosen);

        emailService.sendAssignmentEmail(chosen, affaire);

        log.info("✅ Lawyer '{}' assigned to affaire '{}'", chosen.getNom(), affaire.getTitre());
        log.info("📅 Lawyer '{}' last assigned at {}", chosen.getNom(), chosen.getLastAssignedAt());

        return Optional.of(chosen);
    }

    private Optional<Avocat> getExistingLawyerForAccuse(String nomAccuse) {
        return affaireRepository.findFirstByNomAccuse(nomAccuse)
                .map(Affaire::getAvocatAssigne);
    }

    private boolean hasThisMonthAssignment(Avocat avocat) {
        if (avocat.getLastAssignedAt() == null) return false;
        LocalDate startMonth = LocalDate.now().withDayOfMonth(1);
        return !avocat.getLastAssignedAt().toLocalDate().isBefore(startMonth);
    }
}
