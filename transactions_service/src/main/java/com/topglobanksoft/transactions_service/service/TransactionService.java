package com.topglobanksoft.transactions_service.service;

import com.topglobanksoft.transactions_service.dto.transaction.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
/**
 * Service for transaction operations
 */
public interface TransactionService {
    TransactionDTO performDeposit(String userId, DepositRequestDTO depositRequestDTO);
    TransactionDTO performWithdrawal(String userId, WithdrawalRequestDTO withdrawalRequestDTO);
    TransactionDTO performTransfer(String userIdSender, TransferRequestDTO transferRequestDTO);

    Page<TransactionDTO> getTransactionsForUser(String userId, TransactionFilterDTO filters, Pageable pageable);
    TransactionDTO getTransactionByIdForUser(Long transactionId, String userId);

    Page<TransactionDTO> getAllTransactionsAdmin(TransactionFilterDTO filters, Pageable pageable);
    TransactionDTO getTransactionByIdAdmin(Long transactionId);

    List<TransactionDTO> getTransactionsByUserIdAndDateRange(String queryUserId, LocalDate startDate, LocalDate endDate);

    List<TransactionDTO> getAllTransactionsByDateRange(LocalDate startDate, LocalDate endDate);
}