package com.topglobanksoft.statistics_service.service.impl.client;

import com.topglobanksoft.statistics_service.dto.external.TransactionInfoDTO;
import com.topglobanksoft.statistics_service.exception.ServiceCommunicationException;
import com.topglobanksoft.statistics_service.service.client.TransactionClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionClientServiceImpl implements TransactionClientService {

    @Qualifier("transactionWebClient")
    private final WebClient transactionWebClient;

    @Override
    public Flux<TransactionInfoDTO> getAllTransactionsInfo(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching all transactions info from Transaction Service for period [{} - {}] for statistics", startDate, endDate);

        return transactionWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/admin/all-by-date-range") // Updated URI
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Error from Transaction Service ({}): {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new ServiceCommunicationException(
                                            "Error calling Transaction Service: " + clientResponse.statusCode() + " - " + errorBody));
                                })
                )
                .bodyToFlux(TransactionInfoDTO.class) // Ensure TransactionInfoDTO matches TransactionDTO from transactions_service
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof ServiceCommunicationException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new ServiceCommunicationException("Failed to fetch transactions from Transaction Service after retries.", retrySignal.failure())
                        ))
                .onErrorResume(e -> {
                    log.error("Final error fetching all transactions from Transaction Service: {}", e.getMessage());
                    if (e instanceof ServiceCommunicationException) return Flux.error(e);
                    return Flux.error(new ServiceCommunicationException("Could not obtain all transactions info: " + e.getMessage(), e));
                });
    }
}