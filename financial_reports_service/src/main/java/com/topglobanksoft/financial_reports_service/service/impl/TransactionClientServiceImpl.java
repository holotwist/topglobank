package com.topglobanksoft.financial_reports_service.service.impl;

import com.topglobanksoft.financial_reports_service.dto.TransactionDTO;
import com.topglobanksoft.financial_reports_service.exception.ServiceCommunicationException;
import com.topglobanksoft.financial_reports_service.service.TransactionClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.core.ParameterizedTypeReference; // Not needed for Flux<DTO>
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
// import java.util.List; // Not needed for Flux<DTO>

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionClientServiceImpl implements TransactionClientService {

    private final WebClient transaccionesWebClient;

    @Override
    public Flux<TransactionDTO> getTransactionsByUserAndRange(
            String userId, LocalDate startDate, LocalDate endDate) { // Changed Long to String

        log.debug("Fetching transactions for user {} from {} to {}", userId, startDate, endDate);

        return transaccionesWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user-range")
                        .queryParam("userId", userId) // userId is now String
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Error from Transactions Service ({}): {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new ServiceCommunicationException(
                                            "Error calling Transactions Service: " + clientResponse.statusCode() + " - " + errorBody));
                                })
                )
                .bodyToFlux(TransactionDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof ServiceCommunicationException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            log.error("Retries exhausted for fetching transactions.", retrySignal.failure());
                            return new ServiceCommunicationException("Failed to fetch transactions after multiple retries: " + retrySignal.failure().getMessage(), retrySignal.failure());
                        }))
                .onErrorResume(e -> {
                    log.error("Final error fetching transactions: {}", e.getMessage());
                    if (e instanceof ServiceCommunicationException) {
                        return Flux.error(e);
                    }
                    return Flux.error(new ServiceCommunicationException("Could not obtain transactions: " + e.getMessage(), e));
                });
    }
}
