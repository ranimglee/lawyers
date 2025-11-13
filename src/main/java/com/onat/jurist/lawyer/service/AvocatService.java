package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.dto.in.AvocatRequest;
import com.onat.jurist.lawyer.dto.out.AvocatResponse;

import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvocatService {

    private final AvocatRepository avocatRepository;


    public AvocatResponse createAvocat(AvocatRequest request) {
        Avocat avocat = Avocat.builder()
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .region(request.getRegion())
                .adresse(request.getAdresse())
                .dateInscription(LocalDate.now())
                .lastAssignedAt(LocalDateTime.now().minusDays(1))
                .build();

        Avocat saved = avocatRepository.save(avocat);
        return mapToResponse(saved);
    }


    public List<AvocatResponse> getAllAvocats() {
        return avocatRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public Optional<AvocatResponse> getAvocatById(Long id) {
        return avocatRepository.findById(id).map(this::mapToResponse);
    }


    public AvocatResponse updateAvocat(Long id, AvocatRequest updatedAvocat) {
        Avocat avocat = avocatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avocat non trouvé avec l'id: " + id));

        avocat.setPrenom(updatedAvocat.getPrenom());
        avocat.setNom(updatedAvocat.getNom());
        avocat.setEmail(updatedAvocat.getEmail());
        avocat.setTelephone(updatedAvocat.getTelephone());
        avocat.setRegion(updatedAvocat.getRegion());
        avocat.setAdresse(updatedAvocat.getAdresse());

        Avocat updated = avocatRepository.save(avocat);
        return mapToResponse(updated);
    }


    public void deleteAvocat(Long id) {
        avocatRepository.deleteById(id);
    }


    private AvocatResponse mapToResponse(Avocat avocat) {
        return AvocatResponse.builder()
                .id(avocat.getId())
                .prenom(avocat.getPrenom())
                .nom(avocat.getNom())
                .email(avocat.getEmail())
                .telephone(avocat.getTelephone())
                .region(avocat.getRegion())
                .adresse(avocat.getAdresse())
                .dateInscription(avocat.getDateInscription())
                .affairesAcceptees(avocat.getAffairesAcceptees())
                .affairesRefusees(avocat.getAffairesRefusees())
                .affairesEnCours(avocat.getAffairesEnCours())
                .lastAssignedAt(avocat.getLastAssignedAt())
                .build();
    }
}
