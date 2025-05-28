package com.topglobanksoft.financial_reports_service.dto;

import com.topglobanksoft.financial_reports_service.util.ReportFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

//encapsulates and validates the data sent by the user
public record ReportRequestDTO(
        @NotNull(message = "La fecha de inicio es obligatoria")
        @PastOrPresent(message = "La fecha de inicio no puede ser futura")
        LocalDate startDate,

        @NotNull(message = "La fecha de fin es obligatoria")
        @PastOrPresent(message = "La fecha de fin no puede ser futura")
        LocalDate fechaFin,

        @NotNull(message = "El formato del reporte es obligatorio (PDF o CSV)")
        ReportFormat format // Enum: PDF, CSV
) {}