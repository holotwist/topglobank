package com.topglobanksoft.statistics_service.service.client;

import com.topglobanksoft.statistics_service.dto.external.TransactionInfoDTO;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
/**
 * Client service for transaction data retrieval
 */
public interface TransactionClientService {
    Flux<TransactionInfoDTO> getAllTransactionsInfo(LocalDate desde, LocalDate hasta);
    // Potentially add more specific methods if needed for other stats
}