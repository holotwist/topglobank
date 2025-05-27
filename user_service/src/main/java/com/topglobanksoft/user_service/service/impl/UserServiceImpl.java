package com.topglobanksoft.user_service.service.impl;

import com.topglobanksoft.user_service.dto.UserCreateDTO;
import com.topglobanksoft.user_service.dto.UserDTO;
import com.topglobanksoft.user_service.dto.UserUpdateDTO;
import com.topglobanksoft.user_service.entity.User;
import com.topglobanksoft.user_service.exception.UserAlreadyExistsException;
import com.topglobanksoft.user_service.exception.ResourceNotFoundException;
import com.topglobanksoft.user_service.mapper.UserMapper;
import com.topglobanksoft.user_service.repository.UserRepository;
import com.topglobanksoft.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
// import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder no longer needed here
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    // private final PasswordEncoder passwordEncoder; // Removed

    @Override
    @Transactional
    public UserDTO provisionUserProfile(UserCreateDTO userCreateDTO, Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        if (userRepository.existsById(keycloakId)) {
            throw new UserAlreadyExistsException("User profile for Keycloak ID " + keycloakId + " already exists.");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            // This case might indicate an issue if Keycloak allows email changes that collide
            // or if a user tries to provision a profile for an email already locally mapped to another Keycloak ID.
            throw new UserAlreadyExistsException("User with email " + email + " already has a profile, potentially linked to a different Keycloak account.");
        }

        User user = userMapper.toEntity(userCreateDTO);
        user.setIdUser(keycloakId);
        user.setEmail(email);
        user.setBalance(BigDecimal.ZERO); // Initial balance

        // Extract roles from JWT
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null) {
            @SuppressWarnings("unchecked")
            Collection<String> rolesFromToken = (Collection<String>) realmAccess.get("roles");
            if (rolesFromToken != null && !rolesFromToken.isEmpty()) {
                user.setRoles(rolesFromToken.stream()
                        .map(role -> "ROLE_" + role.toUpperCase())
                        .collect(Collectors.joining(",")));
            } else {
                user.setRoles("ROLE_USER"); // Default if no roles in token
            }
        } else {
            user.setRoles("ROLE_USER"); // Default if no realm_access claim
        }

        User savedUser = userRepository.save(user);
        log.info("User profile provisioned for Keycloak ID: {}, Email: {}", savedUser.getIdUser(), savedUser.getEmail());
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> listAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(String id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Email change is complex with Keycloak as SoT.
        // The email in JWT is source of truth. User service should not allow changing email directly
        // if it's meant to sync from Keycloak. For now, updateEntityFromDto ignores email.
        // If userUpdateDTO.getEmail() is provided and different, it should ideally be ignored or raise error.

        userMapper.updateEntityFromDto(userUpdateDTO, user);
        User updatedUser = userRepository.save(user);
        log.info("User updated: {}", updatedUser.getEmail());
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        // Deleting user in this service. Keycloak user deletion is a separate process.
        // Consider implications: if Keycloak user still exists, they might re-trigger profile provisioning.
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    @Override
    @Transactional
    public void updateUserBalance(String userId, BigDecimal amount, boolean isCredit, String transactionTypeForLog) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {} for balance update.", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId + " for balance update.");
                });

        BigDecimal oldBalance = user.getBalance();
        if (isCredit) {
            user.setBalance(oldBalance.add(amount));
        } else {
            if (oldBalance.compareTo(amount) < 0) {
                log.warn("Insufficient balance for user {}. Current: {}, Tried to debit: {}. Transaction Type: {}",
                        userId, oldBalance, amount, transactionTypeForLog);
                // Not throwing InsufficientBalanceException to avoid Kafka retry loops on persistent state.
                // This negative balance scenario should be flagged for monitoring/ops.
            }
            user.setBalance(oldBalance.subtract(amount));
        }

        try {
            userRepository.save(user);
            log.info("Balance updated for user {}. Old balance: {}, New balance: {}, Amount: {}, Credit: {}",
                    userId, oldBalance, user.getBalance(), amount, isCredit);
        } catch (OptimisticLockingFailureException e) {
            log.warn("Optimistic locking failure while updating balance for user {}. Retrying might be needed or message re-queued.", userId, e);
            throw e; // Let Kafka error handler manage retries/DLT
        }
    }
}