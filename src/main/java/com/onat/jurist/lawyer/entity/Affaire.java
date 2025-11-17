package com.onat.jurist.lawyer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.List;

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

    private LocalDateTime dateCreation;

    private LocalDateTime dateTribunal ;


    @ManyToOne
    @JsonBackReference
    @JsonIgnore
    private Avocat avocatAssigne;

    @Enumerated(EnumType.STRING)
    private StatutAffaire statut ;


    @OneToMany(mappedBy = "affaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EmailNotification> notifications;
}
