package com.topglobanksoft.financial_reports_service.service;

import com.topglobanksoft.financial_reports_service.dto.TransactionDTO;
import reactor.core.publisher.Flux;
import java.time.LocalDate;

public interface TransactionClientService {
    Flux<TransactionDTO> getTransactionsByUserAndRange(
            String usuarioId, LocalDate fechaInicio, LocalDate fechaFin);
}