package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AffaireClotureService {

    private final AffaireRepository affaireRepository;
    private static final Logger log = LoggerFactory.getLogger(AffaireClotureService.class);

    @Transactional
    public void updateAffairesCloturees() {
        LocalDateTime now = LocalDateTime.now();
        List<Affaire> affaires = affaireRepository.findAll()
                .stream()
                .filter(a -> a.getDateTribunal() != null && a.getStatut() != StatutAffaire.CLOTUREE)
                .toList();

        for (Affaire affaire : affaires) {
            if (affaire.getDateTribunal().isBefore(now)) {
                affaire.setStatut(StatutAffaire.CLOTUREE);
                log.info("🔒 Affaire '{}' (id: {}) marked as CLOTURE", affaire.getTitre(), affaire.getId());
            }
        }

        affaireRepository.saveAll(affaires);
    }
}