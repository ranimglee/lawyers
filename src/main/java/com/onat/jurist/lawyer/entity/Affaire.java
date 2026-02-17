package com.onat.jurist.lawyer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "affaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Affaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    private TypeAffaire type;

    @Column(nullable = false)
    private String nomAccuse;

    private LocalDateTime dateCreation;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AssignmentMode assignmentMode = AssignmentMode.AUTOMATIC;

    private LocalDateTime dateTribunal;
    private LocalDateTime assignedAt;

    @ManyToOne
    @JsonIgnore
    private Avocat avocatAssigne;

    @Enumerated(EnumType.STRING)
    private StatutAffaire statut;

    @OneToMany(mappedBy = "affaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EmailNotification> notifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SousTypeAffaire sousType;
}
