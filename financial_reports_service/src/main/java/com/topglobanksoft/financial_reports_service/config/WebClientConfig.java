package com.topglobanksoft.financial_reports_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Value("${app.client.transacciones-service.base-url}")
    private String transaccionesServiceBaseUrl;

    //Defines a Bean that configures the webClient
    @Bean
    public WebClient transaccionesWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(transaccionesServiceBaseUrl)
                // Filter to automatically add JWT token to outgoing calls
                .filter(addJwtTokenFilter())
                .build();
    }

    // Filter to extract JWT token from incoming request and add it to outgoing request
    private ExchangeFilterFunction addJwtTokenFilter() {
        return (clientRequest, next) -> {
            // Attempting to obtain the token from the current security context
            return Mono.deferContextual(contextView -> {
                // Retrieves the token from the security context if it exists.
                return Mono.justOrEmpty(SecurityContextHolder.getContext().getAuthentication())
                        .filter(auth -> auth instanceof JwtAuthenticationToken)
                        .map(auth -> (JwtAuthenticationToken) auth)
                        .map(jwtAuth -> jwtAuth.getToken().getTokenValue())
                        .map(token -> {
                            // Clones the original request and adds the Authorization header
                            return ClientRequest.from(clientRequest)
                                    .headers(headers -> headers.setBearerAuth(token))
                                    .build();
                        })
                        // If there is no token or it is not JWT, simply pass on the original request.
                        .defaultIfEmpty(clientRequest)
                        .flatMap(next::exchange); // Execute the following action on the filter/call chain
            });
        };
    }
}