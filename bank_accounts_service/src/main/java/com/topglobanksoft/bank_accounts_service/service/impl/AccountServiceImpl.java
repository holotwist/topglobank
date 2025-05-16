package com.topglobanksoft.bank_accounts_service.service.impl;

import com.topglobanksoft.bank_accounts_service.dto.AccountCreateDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountUpdateDTO;
import com.topglobanksoft.bank_accounts_service.entity.Account;
import com.topglobanksoft.bank_accounts_service.exception.AccountAccessException;
import com.topglobanksoft.bank_accounts_service.exception.ResourceNotFoundException;
import com.topglobanksoft.bank_accounts_service.mapper.AccountMapper;
import com.topglobanksoft.bank_accounts_service.repository.AccountRepository;
import com.topglobanksoft.bank_accounts_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDTO addAccount(AccountCreateDTO accountCreateDTO, Long userId) {
        if (accountRepository.existsByAccountNumberAndUserId(accountCreateDTO.getAccountNumber(), userId)) {
            throw new IllegalArgumentException("An account with number '" + accountCreateDTO.getAccountNumber() + "' already exists for this user.");
        }
        Account account = accountMapper.toEntity(accountCreateDTO);
        account.setUserId(userId);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountByUser(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountByIdAndUser(Long accountId, Long userId) {
        Account account = accountRepository.findByAccountIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId + " for this user."));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountDTO updateAccount(Long accountId, Long userId, AccountUpdateDTO accountUpdateDTO) {
        Account account = accountRepository.findByAccountIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId + " for this user."));

        // Prevent updating account number to one that already exists for the user, if different from current
        if (accountUpdateDTO.getAccountNumber() != null &&
                !accountUpdateDTO.getAccountNumber().equals(account.getAccountNumber()) &&
                accountRepository.existsByAccountNumberAndUserId(accountUpdateDTO.getAccountNumber(), userId)) {
            throw new IllegalArgumentException("An account with number '" + accountUpdateDTO.getAccountNumber() + "' already exists for this user.");
        }

        accountMapper.updateEntityFromDto(accountUpdateDTO, account);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId, Long userId) {
        Account account = accountRepository.findByAccountIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId + " for this user."));
        accountRepository.delete(account);
    }

    // --- Admin Operations ---

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> listAllAccountsAdmin() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountByIdAdmin(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void deleteAccountAdmin(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id " + accountId);
        }
        accountRepository.deleteById(accountId);
    }
}