package com.topglobanksoft.transactions_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
// import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter; // Not used directly
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
/**
 * Security configuration for transaction service endpoints
 */
public class SecurityConfig {
    /**
     * Configures security rules for all endpoints
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/transactions/deposit").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/transactions/withdraw").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/transactions/transfer").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/transactions/mis-transacciones").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/transactions/{id}").hasRole("USER")
                        .requestMatchers("/api/v1/transactions/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/transactions/user-range").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
    /**
     * Converts JWT roles to Spring Security authorities
     */
    @Bean
    @SuppressWarnings("unchecked")
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(token -> {
            Map<String, Object> realmAccess = token.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                if (roles != null) {
                    return roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList());
                }
            }
            return List.of();
        });
        return converter;
    }
}