package com.onat.jurist.lawyer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientEmail;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String content;
    private boolean success;
    private LocalDateTime sentAt;

    @ManyToOne
    private Affaire affaire;

    private boolean accepted;

    @Column(unique = true, length = 64)
    private String actionToken;

    private LocalDateTime tokenExpiry;

    private LocalDateTime respondedAt;

    @ManyToOne
    private Avocat avocat;
}
