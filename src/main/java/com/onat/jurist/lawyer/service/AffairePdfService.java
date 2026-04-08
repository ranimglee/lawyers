package com.onat.jurist.lawyer.service;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.SousTypeAffaire;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AffairePdfService {

    private final TemplateEngine templateEngine;

    public byte[] generateAffairePdf(Affaire affaire) {
        try {
            // 1️⃣ Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("affaireNomAccuse", fixArabic(affaire.getNomAccuse()));
            context.setVariable("affaireNumero", affaire.getNumero());
            context.setVariable("avocatId", affaire.getAvocatAssigne().getIdentifiant());
            context.setVariable("affaireType", fixArabic(translateSousType(affaire.getSousType())));             context.setVariable("dateTribunal", affaire.getDateTribunal());
            context.setVariable("avocatNom", fixArabic(affaire.getAvocatAssigne().getPrenom()+" "+affaire.getAvocatAssigne().getNom()));
            context.setVariable("avocatAdresse", fixArabic(affaire.getAvocatAssigne().getAdresse()));
            context.setVariable("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            // ✅ Preprocess static Arabic phrases
            context.setVariable("greeting", fixArabic("حضرة الزميل المحترم،\nلي الشرف أن أعلمكم بأني عينتكم للدفاع:"));
            context.setVariable("closing",
                    fixArabic("لذا فرجاء القيام بواجبكم المهني على أحسن وجه وفي الختام تقبلوا فائق اعتبارتي والسلام."));
            context.setVariable("witnessText",fixArabic("بالتسخير المضمن لدى كتابة الفرع الجهوي للمحامين بنابل."));
            context.setVariable("orgName", fixArabic("الهيئة الوطنية للمحامين"));
            context.setVariable("branch", fixArabic("الفرع الجهوي للمحامين بنابل"));

            context.setVariable("dateLabel", fixArabic("نابل في:"));
            context.setVariable("lawyerLabel", fixArabic("الأستاذ(ة):"));
            context.setVariable("registrationLabel", fixArabic("عدد التضمين:"));
            context.setVariable("traineeLawyer", fixArabic("المحامي المتمرن"));
            context.setVariable("addressLabel", fixArabic("العنوان: مكتب الأستاذ(ة):"));
            context.setVariable("forLabel", fixArabic("عن:"));

            context.setVariable("againstLabel", fixArabic("ضد:"));
            context.setVariable("caseLabel", fixArabic("في القضية:"));
            context.setVariable("sessionLabel", fixArabic("بالجلسة التي ستنعقد يوم:"));

            context.setVariable("signatureLabel", fixArabic("الإمضاء:"));
            context.setVariable("presidentLabel", fixArabic("عن رئيس الفرع"));

            context.setVariable("witnessPrefix", fixArabic("يشهد الأستاذ(ة):"));
            context.setVariable("receivedLabel", fixArabic("أنه توصل بتاريخ:"));
            context.setVariable("underNumberLabel", fixArabic("تحت عدد:"));
            context.setVariable("onBehalfLabel", fixArabic("نيابة عن المتهم"));
            context.setVariable("caseNumberLabel", fixArabic("في القضية عدد:"));
            context.setVariable("sessionDayLabel", fixArabic("المعينة لجلسة يوم:"));
            context.setVariable("courtLabel", fixArabic("بمحكمة:"));

            // 2️⃣ Generate HTML using Thymeleaf template
            String html = templateEngine.process("affaire-pdf", context);

            // 3️⃣ Create PDF builder
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.withHtmlContent(html, getClass().getClassLoader().getResource("images/logo-onan.jpg").toExternalForm());
            builder.withHtmlContent(html, getClass().getClassLoader().getResource("images/signature.png").toExternalForm());

            // ⚡ Embed Arabic font
            builder.useFont(() -> getClass().getClassLoader()
                            .getResourceAsStream("fonts/NotoNaskhArabic-Regular.ttf"),
                    "Noto Naskh Arabic");

            // ⚡ Accessibility / PDF settings (optional)
            builder.usePdfUaAccessbility(true);

            // 4️⃣ Output PDF to byte array
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                builder.toStream(os);
                builder.run();
                return os.toByteArray();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }



    public static String fixArabic(String text) {
        try {
            // shape the Arabic letters
            ArabicShaping shaping = new ArabicShaping(ArabicShaping.LETTERS_SHAPE);
            text = shaping.shape(text);

            // apply RTL Bidi
            Bidi bidi = new Bidi(text, Bidi.DIRECTION_RIGHT_TO_LEFT);
            text = bidi.writeReordered(Bidi.DO_MIRRORING);
            return text;
        } catch (Exception e) {
            return text;
        }
    }

    private String translateSousType(SousTypeAffaire sousType) {
        if (sousType == null) return "";

        switch (sousType) {
            // CRIMINEL
            case TRIBUNAL_PREMIERE_INSTANCE_NABEUL:
                return "المحكمة الابتدائية بنابل";
            case TRIBUNAL_PREMIERE_INSTANCE_GROMBALIA:
                return "المحكمة الابتدائية بقرنبالية";
            case COUR_APPEL_NABEUL:
                return "محكمة الاستئناف بنابل";

            // ENQUETE
            case NABEUL:
                return "نابل";
            case ZAGHOUAN:
                return "زغوان";
            case GROMBALIA:
                return "قرنبالية";

            default:
                return sousType.name(); // fallback
        }
    }
}