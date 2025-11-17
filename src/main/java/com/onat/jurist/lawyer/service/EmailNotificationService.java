package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.entity.EmailNotification;
import com.onat.jurist.lawyer.repository.EmailNotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {


    private final EmailNotificationRepository emailRepo;
    private final JavaMailSender mailSender; // configure in application.properties

    @Async
    @Transactional
    public void sendAssignmentEmail(Avocat lawyer, Affaire affaire) {
        String token = UUID.randomUUID().toString();

        String htmlContent = buildEmailBody(affaire, token);

        EmailNotification email = EmailNotification.builder()
                .recipientEmail(lawyer.getEmail())
                .subject("[JURIST] Nouvelle affaire à accepter: " + affaire.getNumero())
                .content(htmlContent)
                .affaire(affaire)
                .success(false)
                .sentAt(LocalDateTime.now())
                .accepted(false)
                .actionToken(token)
                .tokenExpiry(LocalDateTime.now().plusDays(7))
                .build();

        emailRepo.save(email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(lawyer.getEmail());
            helper.setSubject(email.getSubject());
            helper.setText(htmlContent, true); // ← IMPORTANT (HTML)

            mailSender.send(message);
            email.setSuccess(true);

        } catch (Exception ex) {
            email.setSuccess(false);
        }

        emailRepo.save(email);
    }


    private String buildEmailBody(Affaire affaire, String token) {

        String acceptLink = "http://localhost:8080/api/affaires/" + affaire.getId() + "/action?token=" + token + "&decision=accept";
        String rejectLink = "http://localhost:8080/api/affaires/" + affaire.getId() + "/action?token=" + token + "&decision=reject";

        return """
<html>
<body style="font-family: Arial, sans-serif; line-height: 1.6;">
    <h3 style="color:#333;">Nouvelle assignation d'affaire</h3>

    <p>Bonjour Maître,</p>

    <p>Vous avez été sélectionné pour une nouvelle affaire :</p>

    <ul>
        <li><b>Numéro :</b> %s</li>
        <li><b>Titre :</b> %s</li>
        <li><b>Type :</b> %s</li>
        <li><b>Nom de l'accusé :</b> %s</li>
        <li><b>Date du tribunal :</b> %s</li>
    </ul>

    <p>Merci de répondre à cette assignation :</p>

    <p>
        <a href="%s" style="
            display:inline-block;
            padding:12px 20px;
            margin-right:10px;
            background:#28a745;
            color:white;
            font-weight:bold;
            text-decoration:none;
            border-radius:6px;">
            Accepter
        </a>

        <a href="%s" style="
            display:inline-block;
            padding:12px 20px;
            background:#dc3545;
            color:white;
            font-weight:bold;
            text-decoration:none;
            border-radius:6px;">
            Refuser
        </a>
    </p>

    <p>Ce lien est valable 7 jours.</p>

    <p>Cordialement,<br/>L’équipe Jurist</p>
</body>
</html>
""".formatted(
                affaire.getNumero(),
                affaire.getTitre(),
                affaire.getType(),
                affaire.getNomAccuse(),
                affaire.getDateTribunal(),
                acceptLink,
                rejectLink
        );
    }


}