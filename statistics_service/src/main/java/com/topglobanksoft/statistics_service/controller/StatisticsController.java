package com.topglobanksoft.statistics_service.controller;

import com.topglobanksoft.statistics_service.dto.response.AverageBalanceDTO;
import com.topglobanksoft.statistics_service.dto.response.SpendByCategoryDTO;
import com.topglobanksoft.statistics_service.dto.response.UserActivityDTO;
import com.topglobanksoft.statistics_service.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

package com.topglobanksoft.statistics_service.controller;

/**
 * REST controller for statistics operations (admin only)
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Gets spending grouped by categories
     * Defaults to last month if no dates provided
     */
    @GetMapping("/spends-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SpendByCategoryDTO>> getSpendingByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusMonths(1).withDayOfMonth(1);

        List<SpendByCategoryDTO> datos = statisticsService.calculateSpendByCategory(startDate, endDate);
        return ResponseEntity.ok(datos);
    }

    /**
     * Gets most active users list
     * Defaults to last month and top 10 users
     */
    @GetMapping("/active-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserActivityDTO>> getMostActiveUsers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {

        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusMonths(1).withDayOfMonth(1);
        if (limit <= 0) limit = 10;

        List<UserActivityDTO> datos = statisticsService.calculateMostActiveUsers(startDate, endDate, limit);
        return ResponseEntity.ok(datos);
    }

    /**
     * Gets average balance calculation
     */
    @GetMapping("/average-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AverageBalanceDTO> getAverageBalance() {
        AverageBalanceDTO dato = statisticsService.calculateAverageBalance();
        return ResponseEntity.ok(dato);
    }
}