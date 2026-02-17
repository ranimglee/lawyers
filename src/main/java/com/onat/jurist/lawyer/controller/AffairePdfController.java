package com.onat.jurist.lawyer.controller;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.service.AffairePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/affaires/export")
@RequiredArgsConstructor
public class AffairePdfController {

    private final AffaireRepository affaireRepository;
    private final AffairePdfService pdfService;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadAffairePdf(@PathVariable Long id) {

        Affaire affaire = affaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affaire not found"));

        if (affaire.getStatut() != StatutAffaire.ACCEPTEE) {
            throw new RuntimeException("Affaire not accepted yet");
        }

        byte[] pdf = pdfService.generateAffairePdf(affaire);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=affaire_" + affaire.getNumero() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
