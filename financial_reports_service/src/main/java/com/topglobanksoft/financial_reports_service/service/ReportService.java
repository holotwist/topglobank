package com.topglobanksoft.financial_reports_service.service;

import com.topglobanksoft.financial_reports_service.util.ReportFormat;
import java.time.LocalDate;

public interface ReportService {
    byte[] generateReport(String usuarioId, LocalDate fechaInicio, LocalDate fechaFin, ReportFormat formato);
}