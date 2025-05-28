package com.topglobanksoft.statistics_service.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
// import org.springframework.security.core.context.ReactiveSecurityContextHolder; // NO USAR ESTE
import org.springframework.security.core.context.SecurityContextHolder; // USAR ESTE
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
/**
 * Configures WebClient instances for calling other microservices (User and Transaction services)
 * with timeout settings and JWT authentication.
 */
@Configuration
public class WebClientConfig {

    @Value("${app.client.user-service.base-url}")
    private String userServiceBaseUrl;

    @Value("${app.client.transaction-service.base-url}")
    private String transactionServiceBaseUrl;

    // Timeout configuration (5 seconds)
    private final int TIMEOUT = 5000;

    // Creates configured HttpClient with timeout settings
    private HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .responseTimeout(Duration.ofMillis(TIMEOUT))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));
    }

    // WebClient for user service with JWT auth
    @Bean
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(userServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .filter(addJwtTokenFilter())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }

    // WebClient for transaction service with JWT auth
    @Bean
    public WebClient transactionWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(transactionServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .filter(addJwtTokenFilter())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }

    // Adds JWT token from security context to requests
    private ExchangeFilterFunction addJwtTokenFilter() {
        return (clientRequest, next) -> {
            return Mono.deferContextual(contextView -> Mono.justOrEmpty(SecurityContextHolder.getContext().getAuthentication())
                    .filter(auth -> auth instanceof JwtAuthenticationToken)
                    .map(auth -> (JwtAuthenticationToken) auth)
                    .map(jwtAuth -> jwtAuth.getToken().getTokenValue())
                    .map(token -> {
                        return ClientRequest.from(clientRequest)
                                .headers(headers -> headers.setBearerAuth(token))
                                .build();
                    })
                    .defaultIfEmpty(clientRequest)
                    .flatMap(next::exchange));
        };
    }
}