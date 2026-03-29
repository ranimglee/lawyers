package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.entity.EmailNotification;
import com.onat.jurist.lawyer.repository.EmailNotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {


    private final EmailNotificationRepository emailRepo;
    private final JavaMailSender mailSender;

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
                .avocat(lawyer)
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
            helper.setText(htmlContent, true);

            mailSender.send(message);
            email.setSuccess(true);

        } catch (Exception ex) {
            email.setSuccess(false);
            log.error("❌ Failed to send assignment email to {} for affaire {}: {}", lawyer.getEmail(), affaire.getNumero(), ex.getMessage(), ex);
        }


        emailRepo.save(email);
    }


    private String buildEmailBody(Affaire affaire, String token) {

        String acceptLink = "https://lawyers-j1tr.onrender.com/api/affaires/" + affaire.getId() + "/action?token=" + token + "&decision=accept";
        String rejectLink = "https://lawyers-j1tr.onrender.com/api/affaires/" + affaire.getId() + "/action?token=" + token + "&decision=reject";

        return """
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="margin:0; padding:0; background-color:#f4f7fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f7fa; padding:40px 20px;">
        <tr>
            <td align="center">
                <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow:hidden;">
                    
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #1e3c72 0%%, #2a5298 100%%); padding:40px 30px; text-align:center;">
                            <h1 style="color:#ffffff; margin:0; font-size:28px; font-weight:600; letter-spacing:2px;">
                                ⚖️ JURIST
                            </h1>
                            <p style="color:#e8f0fe; margin:10px 0 0 0; font-size:14px;">
                                Système de Gestion Juridique
                            </p>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding:40px 30px;">
                            
                            <div style="background-color:#e3f2fd; border-left:4px solid #2196f3; padding:15px 20px; margin-bottom:30px; border-radius:4px;">
                                <h2 style="color:#1565c0; margin:0 0 5px 0; font-size:20px;">
                                     Nouvelle Assignation d'Affaire
                                </h2>
                                <p style="color:#424242; margin:0; font-size:14px;">
                                    Action requise dans les 7 jours
                                </p>
                            </div>
                            
                            <p style="color:#333333; font-size:16px; line-height:1.6; margin:0 0 25px 0;">
                                Bonjour Maître,
                            </p>
                            
                            <p style="color:#555555; font-size:15px; line-height:1.6; margin:0 0 25px 0;">
                                Vous avez été sélectionné pour représenter une nouvelle affaire. Veuillez consulter les détails ci-dessous :
                            </p>
                            
                            <!-- Case Details Card -->
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#fafafa; border:1px solid #e0e0e0; border-radius:8px; margin:0 0 30px 0;">
                                <tr>
                                    <td style="padding:25px;">
                                        
                                        <table width="100%%" cellpadding="8" cellspacing="0">
                                            <tr>
                                                <td style="color:#757575; font-size:13px; font-weight:600; text-transform:uppercase; width:40%%;">
                                                     Numéro
                                                </td>
                                                <td style="color:#212121; font-size:15px; font-weight:600;">
                                                    %s
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" style="padding:0; height:1px; background-color:#e0e0e0;"></td>
                                            </tr>
                                            <tr>
                                                <td style="color:#757575; font-size:13px; font-weight:600; text-transform:uppercase;">
                                                     Titre
                                                </td>
                                                <td style="color:#212121; font-size:15px;">
                                                    %s
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" style="padding:0; height:1px; background-color:#e0e0e0;"></td>
                                            </tr>
                                            <tr>
                                                <td style="color:#757575; font-size:13px; font-weight:600; text-transform:uppercase;">
                                                     Type d'Affaire
                                                </td>
                                                <td style="color:#212121; font-size:15px;">
                                                    %s
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" style="padding:0; height:1px; background-color:#e0e0e0;"></td>
                                            </tr>
                                            <tr>
                                                <td style="color:#757575; font-size:13px; font-weight:600; text-transform:uppercase;">
                                                     Accusé
                                                </td>
                                                <td style="color:#212121; font-size:15px;">
                                                    %s
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" style="padding:0; height:1px; background-color:#e0e0e0;"></td>
                                            </tr>
                                            <tr>
                                                <td style="color:#757575; font-size:13px; font-weight:600; text-transform:uppercase;">
                                                     Date du Tribunal
                                                </td>
                                                <td style="color:#d32f2f; font-size:15px; font-weight:600;">
                                                    %s
                                                </td>
                                            </tr>
                                        </table>
                                        
                                    </td>
                                </tr>
                            </table>
                            
                            <p style="color:#555555; font-size:15px; line-height:1.6; margin:0 0 25px 0;">
                                Merci de confirmer votre décision en cliquant sur l'un des boutons ci-dessous :
                            </p>
                            
                            <!-- Action Buttons -->
                            <table width="100%%" cellpadding="0" cellspacing="0" style="margin:0 0 30px 0;">
                                <tr>
                                    <td align="center" style="padding:10px;">
                                        <a href="%s" style="
                                            display:inline-block;
                                            padding:16px 40px;
                                            background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%);
                                            color:#ffffff;
                                            font-size:16px;
                                            font-weight:700;
                                            text-decoration:none;
                                            border-radius:8px;
                                            box-shadow: 0 4px 10px rgba(40, 167, 69, 0.3);
                                            text-transform:uppercase;
                                            letter-spacing:1px;">
                                            ✓ Accepter l'Affaire
                                        </a>
                                    </td>
                                    <td align="center" style="padding:10px;">
                                        <a href="%s" style="
                                            display:inline-block;
                                            padding:16px 40px;
                                            background: linear-gradient(135deg, #dc3545 0%%, #c82333 100%%);
                                            color:#ffffff;
                                            font-size:16px;
                                            font-weight:700;
                                            text-decoration:none;
                                            border-radius:8px;
                                            box-shadow: 0 4px 10px rgba(220, 53, 69, 0.3);
                                            text-transform:uppercase;
                                            letter-spacing:1px;">
                                            ✗ Refuser l'Affaire
                                        </a>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Warning Box -->
                            <div style="background-color:#fff3cd; border-left:4px solid #ffc107; padding:15px 20px; margin:0 0 30px 0; border-radius:4px;">
                                <p style="color:#856404; margin:0; font-size:14px; line-height:1.5;">
                                     <strong>Important :</strong> Ce lien d'action expire dans <strong>7 jours</strong>. Veuillez répondre avant l'expiration.
                                </p>
                            </div>
                            
                            <p style="color:#555555; font-size:15px; line-height:1.6; margin:0;">
                                Cordialement,<br/>
                                <strong style="color:#1e3c72;">L'équipe Jurist</strong>
                            </p>
                            
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background-color:#f8f9fa; padding:25px 30px; text-align:center; border-top:1px solid #e0e0e0;">
                            <p style="color:#6c757d; font-size:12px; margin:0 0 5px 0;">
                                Cet email a été envoyé automatiquement par le système Jurist
                            </p>
                            <p style="color:#adb5bd; font-size:11px; margin:0;">
                                © 2025 Jurist - Tous droits réservés
                            </p>
                        </td>
                    </tr>
                    
                </table>
            </td>
        </tr>
    </table>
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