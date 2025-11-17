package com.onat.jurist.lawyer.dto.out;

import com.onat.jurist.lawyer.entity.Affaire;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AvocatResponse {
    private Long id;
    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private String region;
    private String adresse;
    private LocalDate dateInscription;
    private int affairesAcceptees;
    private int affairesRefusees;
    private int affairesEnCours;
    private LocalDateTime lastAssignedAt;
    private List<Affaire> affaires;
}
