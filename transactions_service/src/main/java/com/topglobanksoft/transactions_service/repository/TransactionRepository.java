package com.topglobanksoft.transactions_service.repository;

import com.topglobanksoft.transactions_service.entity.Transaction;
import com.topglobanksoft.transactions_service.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Repository for Transaction entities with custom query methods
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByTransactionIdAndUserId(Long transactionId, String userId);

    Page<Transaction> findByUserIdOrderByDateDesc(String userId, Pageable pageable);

    Page<Transaction> findByUserIdAndTypeOrderByDateDesc(String userId, TransactionType type, Pageable pageable);

    Page<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(String userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    boolean existsByCategoryCategoryId(Long categoryId);
    /**
     * Advanced search with optional filters
     * @param userId Optional user filter
     * @param type Optional transaction type filter
     * @param startDate Optional start date filter (inclusive)
     * @param endDate Optional end date filter (inclusive)
     * @param categoryId Optional category filter
     * @param pageable Pagination parameters
     * @return Filtered and paginated transactions (newest first)
     */
    @Query("SELECT t FROM Transaction t WHERE " +
            "(:userId IS NULL OR t.userId = :userId) AND " + // userId is String now
            "(:type IS NULL OR t.type = :type) AND "           +
            "(:startDate IS NULL OR t.date >= :startDate) AND " +
            "(:endDate IS NULL OR t.date <= :endDate) AND " +
            "(:categoryId IS NULL OR t.category.categoryId = :categoryId) " +
            "ORDER BY t.date DESC")
    Page<Transaction> findTransactionsByCriteria(
            @Param("userId") String userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}