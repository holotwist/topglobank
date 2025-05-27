package com.topglobanksoft.financial_reports_service.generator;

import com.topglobanksoft.financial_reports_service.dto.TransactionDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportGenerator {

    private static final float MARGIN = 50;
    private static final float ROW_HEIGHT = 15;

    public static byte[] generate(List<TransactionDTO> transactions, LocalDate start, LocalDate end) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) { // PDDocument is AutoCloseable
            PDPage currentPage = new PDPage();
            document.addPage(currentPage);
            PDRectangle mediaBox = currentPage.getMediaBox();
            float yPosition = mediaBox.getUpperRightY() - MARGIN;
            float tableWidth = mediaBox.getWidth() - 2 * MARGIN;

            // Define column widths
            float dateColWidth = tableWidth * 0.15f;
            float typeColWidth = tableWidth * 0.15f;
            float amountColWidth = tableWidth * 0.15f;
            float categoryColWidth = tableWidth * 0.20f;
            float descColWidth = tableWidth * 0.35f;

            PDPageContentStream contentStream = new PDPageContentStream(document, currentPage);

            try {
                // Title
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText("Transaction Report (" +
                        start.format(DateTimeFormatter.ISO_DATE) + " - " +
                        end.format(DateTimeFormatter.ISO_DATE) + ")");
                contentStream.endText();
                yPosition -= ROW_HEIGHT * 2.5f; // Space after title

                // Initial Headers
                float headerTextY = yPosition;
                drawPageHeaders(contentStream, MARGIN, headerTextY, tableWidth, ROW_HEIGHT, dateColWidth, typeColWidth, amountColWidth, categoryColWidth, descColWidth);
                yPosition = headerTextY - ROW_HEIGHT; // Move yPosition below header text and its line for the first data row

                // Data
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);

                for (TransactionDTO tx : transactions) {
                    if (yPosition < MARGIN) { // Check if new page is needed (yPosition < bottomMargin)
                        contentStream.close(); // Close stream for the current (full) page

                        currentPage = new PDPage();
                        document.addPage(currentPage);
                        mediaBox = currentPage.getMediaBox(); // Update mediaBox for new page dimensions
                        contentStream = new PDPageContentStream(document, currentPage); // Create new stream for the new page

                        yPosition = mediaBox.getUpperRightY() - MARGIN; // Reset Y to top for the new page

                        // Redraw headers on the new page
                        headerTextY = yPosition;
                        drawPageHeaders(contentStream, MARGIN, headerTextY, tableWidth, ROW_HEIGHT, dateColWidth, typeColWidth, amountColWidth, categoryColWidth, descColWidth);
                        yPosition = headerTextY - ROW_HEIGHT; // Move yPosition below headers for data

                        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9); // Reset font for data
                    }

                    float currentX = MARGIN;
                    // Date
                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, yPosition);
                    contentStream.showText(tx.getDate() != null ? tx.getDate().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "-");
                    contentStream.endText();
                    currentX += dateColWidth;

                    // Type
                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, yPosition);
                    contentStream.showText(tx.getType() != null ? tx.getType() : "-");
                    contentStream.endText();
                    currentX += typeColWidth;

                    // Amount
                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, yPosition);
                    contentStream.showText(tx.getAmount() != null ? String.format("%.2f", tx.getAmount()) : "-");
                    contentStream.endText();
                    currentX += amountColWidth;

                    // Category
                    String categoryName = tx.getCategory() != null && tx.getCategory().getName() != null ? tx.getCategory().getName() : "-";
                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, yPosition);
                    contentStream.showText(truncateText(categoryName, 25));
                    contentStream.endText();
                    currentX += categoryColWidth;

                    // Description
                    String descriptionText = tx.getDescription() != null ? tx.getDescription() : "";
                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, yPosition);
                    contentStream.showText(truncateText(descriptionText, 40));
                    contentStream.endText();

                    yPosition -= ROW_HEIGHT; // Move to next line
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close(); // Ensure the last content stream is closed
                }
            }
            document.save(out);
        } // PDDocument is closed by try-with-resources
        return out.toByteArray();
    }

    private static void drawPageHeaders(PDPageContentStream contentStream, float margin, float headerTextYPosition,
                                        float tableWidth, float rowHeight,
                                        float dateColWidth, float typeColWidth, float amountColWidth,
                                        float categoryColWidth, float descColWidth) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        float currentX = margin;

        // Draw header text
        contentStream.beginText(); contentStream.newLineAtOffset(currentX, headerTextYPosition); contentStream.showText("Date"); contentStream.endText();
        currentX += dateColWidth;
        contentStream.beginText(); contentStream.newLineAtOffset(currentX, headerTextYPosition); contentStream.showText("Type"); contentStream.endText();
        currentX += typeColWidth;
        contentStream.beginText(); contentStream.newLineAtOffset(currentX, headerTextYPosition); contentStream.showText("Amount"); contentStream.endText();
        currentX += amountColWidth;
        contentStream.beginText(); contentStream.newLineAtOffset(currentX, headerTextYPosition); contentStream.showText("Category"); contentStream.endText();
        currentX += categoryColWidth;
        contentStream.beginText(); contentStream.newLineAtOffset(currentX, headerTextYPosition); contentStream.showText("Description"); contentStream.endText();

        // Draw header line below the text
        float lineY = headerTextYPosition - (rowHeight * 0.5f); // Adjust for spacing
        contentStream.moveTo(margin, lineY);
        contentStream.lineTo(margin + tableWidth, lineY);
        contentStream.stroke();
    }

    private static String truncateText(String text, int maxLength) {
        if (text == null) return "-";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}