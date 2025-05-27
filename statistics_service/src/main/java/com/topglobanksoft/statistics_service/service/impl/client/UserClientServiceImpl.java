package com.topglobanksoft.statistics_service.service.impl.client;

import com.topglobanksoft.statistics_service.dto.external.UserInfoDTO;
import com.topglobanksoft.statistics_service.exception.ServiceCommunicationException;
import com.topglobanksoft.statistics_service.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
/**
 * Fetches user data from User Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserClientServiceImpl implements UserClientService {

    @Qualifier("userWebClient") // Specify which WebClient bean to use
    private final WebClient userWebClient;
    /**
     * Gets all users with retry/error handling
     * @return Flux of user info (id, name, balance)
     */
    @Override
    public Flux<UserInfoDTO> getAllUsersInfo() {
        log.debug("Fetching all users info from User Service");
        // Assuming user_service has an endpoint like GET /api/v1/users that returns all users
        // And that user_service.UserDTO includes idUser, fullName, and balance.
        // The WebClient is configured to add JWT token from the current request.
        return userWebClient.get()
                .uri("/") // Assuming the base URL in WebClientConfig points to /api/v1/users
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Error from User Service ({}): {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new ServiceCommunicationException(
                                            "Error calling User Service: " + clientResponse.statusCode() + " - " + errorBody));
                                })
                )
                .bodyToFlux(UserInfoDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof ServiceCommunicationException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new ServiceCommunicationException("Failed to fetch users from User Service after retries.", retrySignal.failure())
                        ))
                .onErrorResume(e -> {
                    log.error("Final error fetching all users from User Service: {}", e.getMessage());
                    if (e instanceof ServiceCommunicationException) return Flux.error(e);
                    return Flux.error(new ServiceCommunicationException("Could not obtain all users info: " + e.getMessage(), e));
                });
    }
}