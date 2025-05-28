package com.topglobanksoft.financial_reports_service.service;

import com.topglobanksoft.financial_reports_service.util.ReportFormat;
import java.time.LocalDate;

public interface ReportService {
    //Generates a report in PDF or CSV format
    byte[] generateReport(String usuarioId, LocalDate fechaInicio, LocalDate fechaFin, ReportFormat formato);
}