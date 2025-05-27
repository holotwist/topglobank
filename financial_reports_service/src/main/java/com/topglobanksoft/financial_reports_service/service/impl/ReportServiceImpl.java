package com.topglobanksoft.financial_reports_service.service.impl;

import com.topglobanksoft.financial_reports_service.dto.TransactionDTO;
import com.topglobanksoft.financial_reports_service.exception.ReportGenerationException;
// import com.topglobanksoft.financial_reports_service.exception.ResourceNotFoundException; // Not used here
import com.topglobanksoft.financial_reports_service.generator.*;
import com.topglobanksoft.financial_reports_service.service.ReportService;
import com.topglobanksoft.financial_reports_service.service.TransactionClientService;
import com.topglobanksoft.financial_reports_service.util.ReportFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final TransactionClientService transactionClientService;

    @Override
    public byte[] generateReport(String userId, LocalDate startDate, LocalDate endDate, ReportFormat format) { // Changed userId to String
        log.info("Generating report: UserID={}, StartDate={}, EndDate={}, Format={}", userId, startDate, endDate, format);

        List<TransactionDTO> transactions;
        try {
            transactions = transactionClientService
                    .getTransactionsByUserAndRange(userId, startDate, endDate)
                    .collectList()
                    .block(); // Blocking for simplicity.
        } catch (Exception e) {
            log.error("Failed to retrieve transactions for report UserID={}: {}", userId, e.getMessage(), e);
            throw new ReportGenerationException("Failed to retrieve transaction data for the report.", e);
        }

        if (transactions == null) {
            log.warn("Transaction data came back as null for UserID={}, Dates=[{} - {}]. Assuming no transactions.", userId, startDate, endDate);
            transactions = Collections.emptyList();
        }

        if (transactions.isEmpty()) {
            log.warn("No transactions found for UserID={}, Dates=[{} - {}]. Generating an empty report.", userId, startDate, endDate);
        }

        log.debug("Number of transactions retrieved for report: {}", transactions.size());

        try {
            if (format == ReportFormat.CSV) {
                log.info("Generating CSV report...");
                return CsvReportGenerator.generate(transactions);
            } else if (format == ReportFormat.PDF) {
                log.info("Generating PDF report...");
                return PdfReportGenerator.generate(transactions, startDate, endDate);
            } else {
                log.error("Unsupported report format requested: {}", format);
                throw new IllegalArgumentException("Unsupported report format: " + format);
            }
        } catch (IOException e) {
            log.error("IO error during {} report generation for UserID={}: {}", format, userId, e.getMessage(), e);
            throw new ReportGenerationException("Error generating " + format + " report due to an IO issue.", e);
        } catch (Exception e) {
            log.error("Unexpected error during {} report generation for UserID={}: {}", format, userId, e.getMessage(), e);
            throw new ReportGenerationException("An unexpected error occurred while generating the " + format + " report.", e);
        }
    }
}