package com.topglobanksoft.bank_accounts_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

//Defines the JPA entity representing bank account in the database
@Entity
@Table(name = "bank_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @NotBlank(message = "Bank name cannot be empty")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String bankName;

    @NotBlank(message = "Account number cannot be empty")
    @Size(max = 50)
    @Column(nullable = false, length = 50) // Consider unique = true if accountNumber should be unique globally or per bank
    private String accountNumber;

    @NotBlank(message = "Account type cannot be empty")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String accountType;

    @NotNull(message = "User ID is required")
    @Column(nullable = false, name = "user_id", length = 36) // Keycloak sub is UUID (String)
    private String userId; // Changed from Long

    @Column(updatable = false)
    private LocalDateTime creationDate;

    private LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}