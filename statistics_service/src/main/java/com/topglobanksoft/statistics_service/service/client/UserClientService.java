package com.topglobanksoft.statistics_service.service.client;

import com.topglobanksoft.statistics_service.dto.external.UserInfoDTO;
import reactor.core.publisher.Flux; // For multiple users
import reactor.core.publisher.Mono; // For a single user if needed
/**
 * Client service for user data retrieval
 */
public interface UserClientService {
    Flux<UserInfoDTO> getAllUsersInfo(); // To get info for all users (e.g., ID, name, balance)
    // Mono<UserInfoDTO> getUserInfoById(Long userId); // If needed for specific user lookups
}