package com.onat.jurist.lawyer.controller;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.service.AcceptanceService;
import com.onat.jurist.lawyer.service.AffaireAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/affaires")
@RequiredArgsConstructor
public class AffaireAssignmentController {


    private final AcceptanceService acceptanceService;
    private final AffaireAssignmentService assignmentService;
    private final AffaireRepository affaireRepository;


    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id, @RequestParam Long avocatId) {
        acceptanceService.acceptAffaire(id, avocatId);
        return ResponseEntity.ok().body("Affaire acceptée");
    }


    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@PathVariable Long id, @RequestParam Long avocatId) {
        acceptanceService.refuseAffaire(id, avocatId);
        return ResponseEntity.ok().body("Affaire refusée et réassignée si possible");
    }


    @PostMapping("/{id}/reassign")
    public ResponseEntity<?> reassign(@PathVariable Long id) {
        Optional<Affaire> a = affaireRepository.findById(id);
        if (a.isEmpty()) return ResponseEntity.notFound().build();
        assignmentService.assignBestLawyer(a.get());
        return ResponseEntity.ok().body("Réassignation provoquée");
    }


    @GetMapping("/{id}/action")
    public ResponseEntity<String> handleAction(
            @PathVariable Long id,
            @RequestParam String token,
            @RequestParam String decision // "accept" ou "reject"
    ) {
        // Vérifier que le token est valide pour cette affaire
        boolean valid = acceptanceService.verifyToken(id, token);
        if (!valid) {
            return ResponseEntity.badRequest().body("Token invalide ou expiré");
        }

        // Récupérer l'affaire
        Optional<Affaire> affaireOpt = affaireRepository.findById(id);
        if (affaireOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Affaire affaire = affaireOpt.get();

        // On suppose que le token correspond à un avocat
        Long avocatId = acceptanceService.getAvocatIdFromToken(token);

        if ("accept".equalsIgnoreCase(decision)) {
            acceptanceService.acceptAffaire(id, avocatId);
            return ResponseEntity.ok("Affaire acceptée");
        } else if ("reject".equalsIgnoreCase(decision)) {
            acceptanceService.refuseAffaire(id, avocatId);
            return ResponseEntity.ok("Affaire refusée et réassignée si possible");
        } else {
            return ResponseEntity.badRequest().body("Décision invalide");
        }
    }

}