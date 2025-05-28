package com.topglobanksoft.statistics_service.service;

import com.topglobanksoft.statistics_service.dto.response.AverageBalanceDTO;
import com.topglobanksoft.statistics_service.dto.response.SpendByCategoryDTO;
import com.topglobanksoft.statistics_service.dto.response.UserActivityDTO;

import java.time.LocalDate;
import java.util.List;
/**
 * Provides business statistics calculations
 */
public interface StatisticsService {
    List<SpendByCategoryDTO> calculateSpendByCategory(LocalDate desde, LocalDate hasta);
    List<UserActivityDTO> calculateMostActiveUsers(LocalDate desde, LocalDate hasta, int limite);
    AverageBalanceDTO calculateAverageBalance();
}