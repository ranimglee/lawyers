package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AffaireAssignmentService {

    private final AvocatRepository avocatRepository;
    private final EmailNotificationService emailService;
    private final AffaireRepository affaireRepository;

    @Transactional
    public Optional<Avocat> assignBestLawyer(Affaire affaire) {
// 1. Priorité: si accusé X a déjà affaire -> même avocat
        Optional<Avocat> prior = getExistingLawyerForAccuse(affaire.getNomAccuse());
        if (prior.isPresent()) {
            Avocat a = prior.get();
            affaire.setAvocatAssigne(a);
            affaireRepository.save(affaire);
            emailService.sendAssignmentEmail(a, affaire);
            return Optional.of(a);
        }


// 2. récupérer tous les avocats
        List<Avocat> list = avocatRepository.findAll();


// 3. trier selon: affairesEnCours asc, hasThisMonthAssignment (false before true), dateInscription desc (newer first), lastAssignedAt asc
        List<Avocat> sorted = list.stream()
                .sorted(Comparator
                        .comparingInt(Avocat::getAffairesEnCours)
                        .thenComparing((Avocat a) -> hasThisMonthAssignment(a) ? 1 : 0)
                        .thenComparing(Avocat::getDateInscription, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(a -> a.getLastAssignedAt() == null ? LocalDateTime.MIN : a.getLastAssignedAt())
                )
                .collect(Collectors.toList());


        if (sorted.isEmpty()) return Optional.empty();


        Avocat chosen = sorted.get(0);
        affaire.setAvocatAssigne(chosen);
        affaireRepository.save(affaire);


// update lastAssignedAt (not increment counters until accept)
        chosen.setLastAssignedAt(LocalDateTime.now());
        avocatRepository.save(chosen);


// send email
        emailService.sendAssignmentEmail(chosen, affaire);


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
