package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AffairePdfService {

    private final TemplateEngine templateEngine;

    public byte[] generateAffairePdf(Affaire affaire) {
        try {
            // 1️⃣ Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("affaire", affaire);
            context.setVariable("avocat", affaire.getAvocatAssigne());
            context.setVariable("date", LocalDate.now());

            String html = templateEngine.process("affaire-pdf", context);

            // 2️⃣ Create PDF renderer builder
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            // 3️⃣ Embed Arabic fonts
            builder.useFont(() -> getClass().getClassLoader()
                            .getResourceAsStream("fonts/Amiri-Regular.ttf"),
                    "Amiri");
            builder.useFont(() -> getClass().getClassLoader()
                            .getResourceAsStream("fonts/Cairo-Regular.ttf"),
                    "Cairo");

            // 4️⃣ Output PDF
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                builder.toStream(os);
                builder.run();
                return os.toByteArray();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
