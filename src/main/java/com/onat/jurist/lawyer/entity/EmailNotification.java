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
    private String content;
    private boolean success;
    private LocalDateTime sentAt = LocalDateTime.now();

    @ManyToOne
    private Affaire affaire;

    private Boolean accepted;  // true if lawyer accepted, false if declined
}
