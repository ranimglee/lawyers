package com.onat.jurist.lawyer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "affaires")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Affaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    private String titre;

    @Enumerated(EnumType.STRING)
    private TypeAffaire type;

    private String nomAccuse;

    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateTribunal ;


    @ManyToOne
    private Avocat avocatAssigne;

    @Enumerated(EnumType.STRING)
    private StatutAffaire statut = StatutAffaire.EN_ATTENTE;



}
