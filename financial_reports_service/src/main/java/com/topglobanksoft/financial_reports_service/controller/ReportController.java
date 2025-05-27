package com.topglobanksoft.financial_reports_service.controller;

import com.topglobanksoft.financial_reports_service.service.ReportService;
import com.topglobanksoft.financial_reports_service.util.ReportFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    private String getUserIdFromToken(Jwt jwt) { // Changed return type to String
        String userId = jwt.getSubject();
        if (userId == null || userId.isBlank()) {
            log.error("Keycloak 'sub' claim (userId) is missing or blank in JWT: {}", jwt.getClaims());
            throw new IllegalArgumentException("User ID claim ('sub') not found or invalid in JWT token");
        }
        return userId;
    }

    @GetMapping("/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("format") ReportFormat format,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserIdFromToken(jwt);
        log.info("Received report generation request: UserID={}, StartDate={}, EndDate={}, Format={}",
                userId, startDate, endDate, format);

        if (startDate.isAfter(endDate)) {
            log.warn("Invalid date range for report: startDate {} is after endDate {}", startDate, endDate);
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        byte[] reporteBytes = reportService.generateReport(userId, startDate, endDate, format);

        HttpHeaders headers = new HttpHeaders();
        String filename = String.format("report_%s_%s.%s",
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE),
                format.name().toLowerCase());

        headers.setContentDispositionFormData("attachment", filename);

        if (format == ReportFormat.PDF) {
            headers.setContentType(MediaType.APPLICATION_PDF);
        } else if (format == ReportFormat.CSV) {
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=utf-8"));
        } else {
            log.error("Unknown report format specified for content type: {}", format);
            return ResponseEntity.badRequest().body("Unsupported report format for Content-Type header.".getBytes());
        }

        log.info("Report generated successfully: Filename={}", filename);
        return ResponseEntity.ok()
                .headers(headers)
                .body(reporteBytes);
    }
}