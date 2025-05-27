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

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/spends-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SpendByCategoryDTO>> getSpendingByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Set default values if not provided (e.g. last month)
        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusMonths(1).withDayOfMonth(1);

        List<SpendByCategoryDTO> datos = statisticsService.calculateSpendByCategory(startDate, endDate);
        return ResponseEntity.ok(datos);
    }

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

    @GetMapping("/average-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AverageBalanceDTO> getAverageBalance() {
        AverageBalanceDTO dato = statisticsService.calculateAverageBalance();
        return ResponseEntity.ok(dato);
    }
}