package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.dto.in.AffaireRequestDTO;
import com.onat.jurist.lawyer.dto.out.AffaireResponseDTO;
import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AffaireService {

    private static final Logger log = LoggerFactory.getLogger(AffaireService.class);

    private final AffaireRepository affaireRepository;
    private final AffaireAssignmentService assignmentService;

    // Create new affair
    public AffaireResponseDTO createAffaire(AffaireRequestDTO dto) {
        if (affaireRepository.existsByNumero(dto.getNumero())) {
            log.warn("⚠️ Attempt to create duplicate affaire with numero '{}'", dto.getNumero());
            throw new IllegalArgumentException("Affaire with numero " + dto.getNumero() + " already exists.");
        }

        Affaire affaire = Affaire.builder()
                .numero(dto.getNumero())
                .titre(dto.getTitre())
                .type(dto.getType())
                .nomAccuse(dto.getNomAccuse())
                .dateTribunal(dto.getDateTribunal())
                .dateCreation(LocalDateTime.now())
                .statut(com.onat.jurist.lawyer.entity.StatutAffaire.EN_ATTENTE)
                .build();

        affaireRepository.save(affaire);
        log.info("🆕 Affaire '{}' created with numero '{}'", affaire.getTitre(), affaire.getNumero());

        // Assignation
        Optional<Avocat> assigned = assignmentService.assignBestLawyer(affaire);
        assigned.ifPresent(a -> log.info("🔄 Lawyer '{}' assigned to new affaire '{}'", a.getNom(), affaire.getTitre()));
        if (assigned.isEmpty()) log.warn("⚠️ No available lawyer for new affaire '{}'", affaire.getTitre());

        return mapToDTO(affaire);
    }

    // Get all affairs
    public List<AffaireResponseDTO> getAllAffaires() {
        log.info("📄 Retrieving all affaires");
        return affaireRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Get one by ID
    public AffaireResponseDTO getAffaireById(Long id) {
        Affaire affaire = affaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affaire not found with id " + id));
        log.info("🔍 Retrieved affaire '{}' with id {}", affaire.getTitre(), id);
        return mapToDTO(affaire);
    }

    // Update affair
    public AffaireResponseDTO updateAffaire(Long id, AffaireRequestDTO dto) {
        Affaire affaire = affaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affaire not found with id " + id));

        affaire.setNumero(dto.getNumero());
        affaire.setTitre(dto.getTitre());
        affaire.setType(dto.getType());
        affaire.setNomAccuse(dto.getNomAccuse());
        affaire.setDateTribunal(dto.getDateTribunal());

        affaireRepository.save(affaire);
        log.info("✏️ Updated affaire '{}' (id: {})", affaire.getTitre(), id);
        return mapToDTO(affaire);
    }

    // Delete affair
    public void deleteAffaire(Long id) {
        if (!affaireRepository.existsById(id)) {
            log.warn("⚠️ Attempt to delete non-existent affaire with id {}", id);
            throw new EntityNotFoundException("Affaire not found with id " + id);
        }
        affaireRepository.deleteById(id);
        log.info("🗑️ Deleted affaire with id {}", id);
    }

    private AffaireResponseDTO mapToDTO(Affaire affaire) {
        return AffaireResponseDTO.builder()
                .id(affaire.getId())
                .numero(affaire.getNumero())
                .titre(affaire.getTitre())
                .type(affaire.getType())
                .nomAccuse(affaire.getNomAccuse())
                .dateCreation(affaire.getDateCreation())
                .dateTribunal(affaire.getDateTribunal())
                .statut(affaire.getStatut())
                .avocatId(affaire.getAvocatAssigne() != null ? affaire.getAvocatAssigne().getId() : null)
                .avocatNom(affaire.getAvocatAssigne() != null ? affaire.getAvocatAssigne().getNom() + " " + affaire.getAvocatAssigne().getPrenom() : null)
                .build();
    }

    public List<Affaire> getAffairesByAvocat(Long avocatId) {
        log.info("👩‍⚖️ Retrieving affaires for lawyer with id {}", avocatId);
        return affaireRepository.findAllByAvocatId(avocatId);
    }
}
