package com.topglobanksoft.bank_accounts_service.repository;

import com.topglobanksoft.bank_accounts_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Find all accounts for a specific user
    List<Account> findByUserId(Long userId);

    // Finds a specific account by its ID and the ID of the user owner
    Optional<Account> findByAccountIdAndUserId(Long accountId, Long userId);

    // Verifies if a specific account belongs to a user
    boolean existsByAccountIdAndUserId(Long accountId, Long userId);

    // It could be useful to avoid duplicates per user
    boolean existsByAccountNumberAndUserId(String accountNumber, Long userId);
}