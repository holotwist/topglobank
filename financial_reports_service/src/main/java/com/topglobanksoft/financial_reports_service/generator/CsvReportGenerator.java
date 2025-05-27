package com.topglobanksoft.financial_reports_service.generator;

import com.topglobanksoft.financial_reports_service.dto.TransactionDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvReportGenerator {
    public static byte[] generate(List<TransactionDTO> transactions) throws IOException {
        final String[] HEADERS = { "Transaction ID", "Date", "Type", "Amount", "Description", "Category Name" };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Write UTF-8 BOM for better Excel compatibility with special characters
        out.write(0xEF);
        out.write(0xBB);
        out.write(0xBF);

        try (OutputStreamWriter streamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(streamWriter,
                     CSVFormat.DEFAULT.builder()
                             .setHeader(HEADERS)
                             .setQuoteMode(QuoteMode.MINIMAL) // Quote only if necessary
                             .build())) {

            for (TransactionDTO tx : transactions) {
                csvPrinter.printRecord(
                        tx.getTransactionId(),
                        tx.getDate() != null ? tx.getDate().format(DateTimeFormatter.ISO_DATE_TIME) : "",
                        tx.getType(),
                        tx.getAmount(),
                        tx.getDescription(),
                        tx.getCategory() != null && tx.getCategory().getName() != null ? tx.getCategory().getName() : ""
                );
            }
            csvPrinter.flush();
        }
        return out.toByteArray();
    }
}