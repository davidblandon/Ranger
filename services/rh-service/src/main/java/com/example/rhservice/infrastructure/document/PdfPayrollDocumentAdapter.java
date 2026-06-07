package com.example.rhservice.infrastructure.document;

import com.example.rhservice.application.port.out.PayrollDocumentPort;
import com.example.rhservice.domain.model.Payroll;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class PdfPayrollDocumentAdapter implements PayrollDocumentPort {

    @Override
    public byte[] generatePayrollPaystub(Payroll payroll) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                stream.newLineAtOffset(50, 720);
                stream.showText("Payroll Paystub");
                stream.newLineAtOffset(0, -25);
                stream.setFont(PDType1Font.HELVETICA, 12);
                stream.showText("Employee: " + payroll.getEmployee().getName());
                stream.newLineAtOffset(0, -18);
                stream.showText("Month: " + payroll.getMonth());
                stream.newLineAtOffset(0, -18);
                stream.showText("Year: " + payroll.getYear());
                stream.newLineAtOffset(0, -18);
                stream.showText("Amount: $" + String.format("%.2f", payroll.getAmount()));
                stream.newLineAtOffset(0, -18);
                stream.showText("Paid: " + (payroll.isPaid() ? "Yes" : "No"));
                stream.newLineAtOffset(0, -18);
                stream.showText("Generated: " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(java.time.LocalDateTime.now()));
                stream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to generate payroll paystub PDF", e);
        }
    }
}
