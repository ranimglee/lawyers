package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.entity.TypeAffaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnqueteScheduler {


    private final AffaireRepository affaireRepository;
    private final AffaireAssignmentService assignmentService;


    // runs every minute and reassigns enquete cases that are still EN_ATTENTE and older than 5 minutes
    @Scheduled(fixedRate = 60000)
    public void reassignStaleEnquetes() {
        LocalDateTime fiveMinAgo = LocalDateTime.now().minusMinutes(5);
        List<Affaire> stale = affaireRepository.findWaitingByTypeAndStatutBefore(TypeAffaire.ENQUETE, StatutAffaire.EN_ATTENTE, fiveMinAgo);
        for (Affaire a : stale) {
// only reassign if still EN_ATTENTE
            if (a.getStatut() == StatutAffaire.EN_ATTENTE) {
// remove current assignee and try assigning next
                a.setAvocatAssigne(null);
                affaireRepository.save(a);
                assignmentService.assignBestLawyer(a);
            }
        }
    }
}
