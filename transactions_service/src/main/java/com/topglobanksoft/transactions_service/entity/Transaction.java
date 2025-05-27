package com.topglobanksoft.transactions_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Size(max = 255)
    private String description;

    @NotNull
    @Column(nullable = false, name = "user_id", length = 36) // Keycloak sub (UUID)
    private String userId;

    @Column(name = "source_user_id", length = 36)
    private String sourceUserId;

    @Column(name = "destination_user_id", length = 36)
    private String destinationUserId;

    @Column(name = "source_account_id")
    private Long sourceAccountId;

    @Column(name = "destination_account_id")
    private Long destinationAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "related_transaction_id")
    private Long relatedTransactionId;
}