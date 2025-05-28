package com.topglobanksoft.statistics_service.service.impl;

import com.topglobanksoft.statistics_service.dto.external.TransactionInfoDTO;
import com.topglobanksoft.statistics_service.dto.external.UserInfoDTO;
import com.topglobanksoft.statistics_service.dto.response.AverageBalanceDTO;
import com.topglobanksoft.statistics_service.dto.response.SpendByCategoryDTO;
import com.topglobanksoft.statistics_service.dto.response.UserActivityDTO;
import com.topglobanksoft.statistics_service.service.StatisticsService;
import com.topglobanksoft.statistics_service.service.client.TransactionClientService;
import com.topglobanksoft.statistics_service.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Core statistics calculation service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final UserClientService userClientService;
    private final TransactionClientService transactionClientService;

    private static final List<String> EXPENSE_TYPES = List.of("WITHDRAWAL", "TRANSFER_SENT");

    /**
     * Calculates spending grouped by category (cached)
     */
    @Override
    @Cacheable(value = "spendsCategory", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<SpendByCategoryDTO> calculateSpendByCategory(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating expenses by category for period [{} - {}]", startDate, endDate);

        List<TransactionInfoDTO> transactions = transactionClientService
                .getAllTransactionsInfo(startDate, endDate)
                .filter(tx -> tx.getAmount() != null && tx.getAmount().signum() > 0 &&
                        tx.getType() != null && EXPENSE_TYPES.contains(tx.getType().toUpperCase()) &&
                        tx.getCategoryName() != null && !tx.getCategoryName().isBlank())
                .collectList()
                .block();

        if (transactions == null || transactions.isEmpty()) {
            log.info("No relevant expense transactions found for period [{} - {}]", startDate, endDate);
            return Collections.emptyList();
        }

        Map<String, BigDecimal> gastosPorCategoria = transactions.stream()
                .collect(Collectors.groupingBy(
                        TransactionInfoDTO::getCategoryName,
                        Collectors.mapping(TransactionInfoDTO::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return gastosPorCategoria.entrySet().stream()
                .map(entry -> new SpendByCategoryDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(SpendByCategoryDTO::getTotalSpent).reversed())
                .collect(Collectors.toList());
    }
    /**
     * Finds most active users by transaction count (cached)
     */
    @Override
    @Cacheable(value = "activeUsers", key = "#startDate.toString() + '-' + #endDate.toString() + '-' + #limit")
    public List<UserActivityDTO> calculateMostActiveUsers(LocalDate startDate, LocalDate endDate, int limit) {
        log.info("Calculating most active users for period [{} - {}], limit {}", startDate, endDate, limit);

        List<TransactionInfoDTO> transactions = transactionClientService
                .getAllTransactionsInfo(startDate, endDate)
                .collectList()
                .block();

        if (transactions == null || transactions.isEmpty()) {
            log.info("No transactions found for activity calculation in period [{} - {}]", startDate, endDate);
            return Collections.emptyList();
        }

        Map<String, Long> conteoPorUsuario = transactions.stream() // Key is String (userId)
                .filter(tx -> tx.getUserId() != null && !tx.getUserId().isBlank())
                .collect(Collectors.groupingBy(
                        TransactionInfoDTO::getUserId,
                        Collectors.counting()
                ));

        Map<String, String> userNames; // Key and Value are String
        try {
            userNames = userClientService.getAllUsersInfo()
                    .filter(userInfo -> userInfo.getIdUser() != null && !userInfo.getIdUser().isBlank() &&
                            conteoPorUsuario.containsKey(userInfo.getIdUser()))
                    .collectMap(UserInfoDTO::getIdUser, UserInfoDTO::getFullName)
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch user names for active users calculation: {}", e.getMessage(), e);
            userNames = Collections.emptyMap();
        }

        final Map<String, String> finalUserNames = userNames != null ? userNames : Collections.emptyMap();

        return conteoPorUsuario.entrySet().stream()
                .map(entry -> new UserActivityDTO(
                        entry.getKey(), // String userId
                        finalUserNames.getOrDefault(entry.getKey(), "Usuario ID: " + entry.getKey()),
                        entry.getValue()
                ))
                .sorted(Comparator.comparingLong(UserActivityDTO::getTransactionsQuantity).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    /**
     * Calculates average account balance (cached)
     */
    @Override
    @Cacheable("averageBalance")
    public AverageBalanceDTO calculateAverageBalance() {
        log.info("Calculating average user balance");

        List<UserInfoDTO> users = userClientService.getAllUsersInfo()
                .filter(userInfo -> userInfo.getBalance() != null)
                .collectList()
                .block();

        if (users == null || users.isEmpty()) {
            log.info("No users found or no users with balance information for average balance calculation.");
            return new AverageBalanceDTO(BigDecimal.ZERO, 0);
        }

        BigDecimal totalSumBalances = users.stream()
                .map(UserInfoDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (users.isEmpty()) {
            return new AverageBalanceDTO(BigDecimal.ZERO, 0);
        }

        BigDecimal averageBalance = totalSumBalances.divide(
                BigDecimal.valueOf(users.size()),
                2,
                RoundingMode.HALF_UP
        );

        return new AverageBalanceDTO(averageBalance, users.size());
    }
}