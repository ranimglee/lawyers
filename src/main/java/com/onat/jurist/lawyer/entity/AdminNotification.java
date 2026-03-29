package com.onat.jurist.lawyer.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean seen = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private Affaire affaire;
}
