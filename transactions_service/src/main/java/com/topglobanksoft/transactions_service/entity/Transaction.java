package com.topglobanksoft.transactions_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Represents a financial transaction
 */
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_user_id", columnList = "user_id"),
        @Index(name = "idx_transaction_date", columnList = "date"),
        @Index(name = "idx_transaction_type", columnList = "type"),
        @Index(name = "idx_transaction_category_id", columnList = "category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    /**
     * Unique transaction identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    /**
     * Transaction timestamp (auto-set to current time)
     */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();
    /**
     * Transaction type (DEPOSIT, WITHDRAWAL, TRANSFER)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    /**
     * Transaction amount (precision: 15 digits, scale: 2 decimals)
     */
    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    /**
     * Optional transaction description
     */
    @Size(max = 255)
    private String description;
    /**
     * User ID who initiated the transaction (Keycloak UUID)
     */
    @NotNull
    @Column(nullable = false, name = "user_id", length = 36) // Keycloak sub (UUID)
    private String userId;
    /**
     * Source user ID (for transfers)
     */
    @Column(name = "source_user_id", length = 36)
    private String sourceUserId;
    /**
     * Destination user ID (for transfers)
     */
    @Column(name = "destination_user_id", length = 36)
    private String destinationUserId;
    /**
     * Source account ID (for transfers)
     */
    @Column(name = "source_account_id")
    private Long sourceAccountId;
    /**
     * Destination account ID (for transfers)
     */
    @Column(name = "destination_account_id")
    private Long destinationAccountId;
    /**
     * Transaction category (optional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    /**
     * Related transaction ID (for transfers)
     */
    @Column(name = "related_transaction_id")
    private Long relatedTransactionId;
}