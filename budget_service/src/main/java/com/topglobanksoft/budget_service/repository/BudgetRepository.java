package com.topglobanksoft.budget_service.repository;

import com.topglobanksoft.budget_service.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndYearAndMonthOrderByCategoryId(String userId, Integer year, Integer month); // Changed

    Page<Budget> findByUserIdOrderByYearDescMonthDescNameAsc(String userId, Pageable pageable); // Changed

    Optional<Budget> findByBudgetIdAndUserId(Long idBudget, String userId); // Changed

    Optional<Budget> findByUserIdAndCategoryIdAndYearAndMonth(
            String userId, Long categoryId, Integer year, Integer month); // Changed

    @Modifying
    @Query("UPDATE Budget b SET b.amountSpent = b.amountSpent + :spentAmount, b.version = b.version + 1 " +
            "WHERE b.userId = :userId AND b.categoryId = :categoryId AND b.year = :year AND b.month = :month " +
            "AND b.version = :expectedVersion")
    int updateAmountSpentWithOptimisticLock(
            @Param("userId") String userId, // Changed
            @Param("categoryId") Long categoryId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("spentAmount") BigDecimal spentAmount,
            @Param("expectedVersion") Long expectedVersion);


    boolean existsByUserIdAndCategoryIdAndYearAndMonth(
            String userId, Long categoryId, Integer year, Integer month); // Changed
}