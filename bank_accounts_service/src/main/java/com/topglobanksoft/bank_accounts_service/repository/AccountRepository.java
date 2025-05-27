package com.topglobanksoft.bank_accounts_service.repository;

import com.topglobanksoft.bank_accounts_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(String userId);

    Optional<Account> findByAccountIdAndUserId(Long accountId, String userId);

    boolean existsByAccountIdAndUserId(Long accountId, String userId);

    boolean existsByAccountNumberAndUserId(String accountNumber, String userId);
}