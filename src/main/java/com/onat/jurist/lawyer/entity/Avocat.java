package com.onat.jurist.lawyer.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avocats")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

public class Avocat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prenom;

    private String nom;

    private String email;

    private String telephone;

    private String region;

    private String adresse;
    private LocalDate dateInscription;
    private int affairesAcceptees = 0;
    private int affairesRefusees = 0;
    private int affairesEnCours = 0;
    private LocalDateTime lastAssignedAt;

    @OneToMany(mappedBy = "avocatAssigne", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonManagedReference
    private List<Affaire> affaires = new ArrayList<>();
}
