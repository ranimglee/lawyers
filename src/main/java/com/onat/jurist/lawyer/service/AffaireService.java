package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.dto.in.AffaireRequestDTO;
import com.onat.jurist.lawyer.dto.out.AffaireResponseDTO;
import com.onat.jurist.lawyer.entity.*;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
        import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AffaireService {

    private static final Logger log = LoggerFactory.getLogger(AffaireService.class);

    private final AffaireRepository affaireRepository;
    private final AffaireAssignmentService assignmentService;
    private final EmailNotificationService emailService;
    private final AvocatRepository avocatRepository;
    private final AffaireClotureService affaireClotureService;


    @Transactional
    public AffaireResponseDTO createAffaire(AffaireRequestDTO dto) {
        validateAffaireRequest(dto);

        if (affaireRepository.existsByNumero(dto.getNumero())) {
            log.warn("⚠️ Attempt to create duplicate affaire with numero '{}'", dto.getNumero());
            throw new IllegalArgumentException(
                    "Affaire with numero " + dto.getNumero() + " already exists."
            );
        }

        Affaire affaire = Affaire.builder()
                .numero(dto.getNumero())
                .titre(dto.getTitre())
                .type(dto.getType())
                .nomAccuse(dto.getNomAccuse())
                .dateTribunal(dto.getDateTribunal())
                .dateCreation(LocalDateTime.now())
                .assignedAt(LocalDateTime.now())
                .statut(StatutAffaire.EN_ATTENTE)
                .sousType(dto.getSousType())
                .build();

        affaireRepository.save(affaire);
        log.info("🆕 Affaire '{}' created with numero '{}'", affaire.getTitre(), affaire.getNumero());

        Optional<Avocat> assigned;

        // 🔀 ASSIGNMENT MODE
        if (dto.getAssignmentMode() == AssignmentMode.MANUAL) {
            assigned = assignManually(affaire, dto.getAvocatId());
        } else {
            assigned = assignmentService.assignBestLawyer(affaire);
        }

        assigned.ifPresentOrElse(
                a -> log.info("🔄 Lawyer '{}' assigned to new affaire '{}'", a.getNom(), affaire.getTitre()),
                () -> log.warn("⚠️ No available lawyer for new affaire '{}'", affaire.getTitre())
        );

        return mapToDTO(affaire);
    }

    private Optional<Avocat> assignManually(Affaire affaire, Long avocatId) {
        if (avocatId == null) {
            throw new IllegalArgumentException("Avocat ID is required for manual assignment");
        }

        Avocat avocat = avocatRepository.findById(avocatId)
                .orElseThrow(() -> new EntityNotFoundException("Avocat not found"));

        affaire.setAvocatAssigne(avocat);
        affaire.setAssignedAt(LocalDateTime.now());
        affaireRepository.save(affaire);

        avocat.setLastAssignedAt(LocalDateTime.now());
        avocatRepository.save(avocat);

        emailService.sendAssignmentEmail(avocat, affaire);

        log.info("🧑 Manually assigned lawyer '{}' to affaire '{}'",
                avocat.getNom(), affaire.getTitre());

        return Optional.of(avocat);
    }



    public AffaireResponseDTO updateAffaire(Long id, AffaireRequestDTO dto) {
        validateAffaireRequest(dto);

        Affaire affaire = affaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affaire not found with id " + id));


        affaire.setNumero(dto.getNumero());
        affaire.setTitre(dto.getTitre());
        affaire.setType(dto.getType());
        affaire.setNomAccuse(dto.getNomAccuse());
        affaire.setDateTribunal(dto.getDateTribunal());
        affaire.setSousType(dto.getSousType());

        affaireRepository.save(affaire);
        log.info("✏️ Updated affaire '{}' (id: {})", affaire.getTitre(), id);
        return mapToDTO(affaire);
    }


    // ----- VALIDATION -----
    private void validateAffaireRequest(AffaireRequestDTO dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Type d'affaire ne peut pas être null");
        }

        if (dto.getSousType() != null && !isSousTypeValidForType(dto.getSousType(), dto.getType())) {
            throw new IllegalArgumentException("Sous-type " + dto.getSousType() + " invalide pour le type " + dto.getType());
        }
    }

    private boolean isSousTypeValidForType(SousTypeAffaire sousType, TypeAffaire type) {
        return switch (type) {
            case CRIMINEL -> List.of(
                    SousTypeAffaire.TRIBUNAL_PREMIERE_INSTANCE_NABEUL,
                    SousTypeAffaire.TRIBUNAL_PREMIERE_INSTANCE_KORBA,
                    SousTypeAffaire.COUR_APPEL_NABEUL
            ).contains(sousType);
            case ENQUETE -> List.of(
                    SousTypeAffaire.NABEUL,
                    SousTypeAffaire.ZAGHOUAN,
                    SousTypeAffaire.GROMBALIA
            ).contains(sousType);
            default -> false;
        };
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
                .sousType(affaire.getSousType())
                .build();
    }

    public Map<SousTypeAffaire, String> getSousTypesMap(TypeAffaire type) {
        return Arrays.stream(SousTypeAffaire.values())
                .filter(st -> isSousTypeValidForType(st, type))
                .collect(Collectors.toMap(st -> st, this::formatLabel));
    }

    private String formatLabel(SousTypeAffaire st) {
        return switch (st) {
            case TRIBUNAL_PREMIERE_INSTANCE_NABEUL -> "Tribunal de première instance de Nabeul";
            case TRIBUNAL_PREMIERE_INSTANCE_KORBA -> "Tribunal de première instance de Korba";
            case COUR_APPEL_NABEUL -> "Cour d'appel de Nabeul";
            case NABEUL -> "Nabeul";
            case ZAGHOUAN -> "Zaghouan";
            case GROMBALIA -> "Grombalia";
        };
    }

    // Get one by ID
    public AffaireResponseDTO getAffaireById(Long id) {
        Affaire affaire = affaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affaire not found with id " + id));
        log.info("🔍 Retrieved affaire '{}' with id {}", affaire.getTitre(), id);
        return mapToDTO(affaire);
    }
    // Get all affairs
    public List<AffaireResponseDTO> getAllAffaires() {
        log.info("📄 Retrieving all affaires");
        return affaireRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
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

    public List<AffaireResponseDTO> getAffairesByAvocat(Long avocatId) {
        log.info("📄 Retrieving affaires for lawyer with id {}", avocatId);
        return affaireRepository.findAllByAvocatId(avocatId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public long countAffaires() {
        return affaireRepository.count();
    }

    public Map<String, Long> countAffairesByStatus() {

        return affaireRepository.countAffairesByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((StatutAffaire) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }

    public Map<String, Long> countAffairesByType() {
        return affaireRepository.countAffairesByType()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((TypeAffaire) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }
    public double calculateAverageHandlingTime() {
        return affaireRepository.findAll()
                .stream()
                .filter(a -> a.getAssignedAt() != null && a.getDateTribunal() != null)
                .mapToDouble(a -> Duration.between(a.getAssignedAt(), a.getDateTribunal()).toHours())
                .average()
                .orElse(0.0);
    }

    public Map<String, Long> countAssignedVsUnassigned() {
        long assigned = affaireRepository.countByAvocatAssigneIsNotNull();
        long unassigned = affaireRepository.countByAvocatAssigneIsNull();
        Map<String, Long> map = new HashMap<>();
        map.put("assigned", assigned);
        map.put("unassigned", unassigned);
        return map;
    }
    public Map<String, Long> countAffairesByRegion() {
        return affaireRepository.findAll()
                .stream()
                .filter(a -> a.getAvocatAssigne() != null)
                .collect(Collectors.groupingBy(a -> a.getAvocatAssigne().getRegion(), Collectors.counting()));
    }

    public Map<String, Long> countAffairesPerMonth() {
        return affaireRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDateCreation().getMonth().toString() + "-" + a.getDateCreation().getYear(),
                        Collectors.counting()
                ));
    }


    @Scheduled(cron = "0 0 * * * *")
    public void markAffairesAsClosed() {
        affaireClotureService.updateAffairesCloturees();
    }
}
