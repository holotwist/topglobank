package com.topglobanksoft.transactions_service.service.impl;

import com.topglobanksoft.transactions_service.dto.event.BalanceUpdateEventDTO;
import com.topglobanksoft.transactions_service.dto.transaction.*;
import com.topglobanksoft.transactions_service.entity.Category;
import com.topglobanksoft.transactions_service.entity.Transaction;
import com.topglobanksoft.transactions_service.entity.TransactionType;
import com.topglobanksoft.transactions_service.exception.ResourceNotFoundException;
import com.topglobanksoft.transactions_service.exception.TransactionProcessingException;
import com.topglobanksoft.transactions_service.mapper.TransactionMapper;
import com.topglobanksoft.transactions_service.repository.CategoryRepository;
import com.topglobanksoft.transactions_service.repository.TransactionRepository;
import com.topglobanksoft.transactions_service.service.KafkaProducerService;
import com.topglobanksoft.transactions_service.service.TransactionService;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
// import java.util.Optional; // Not directly used
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public TransactionDTO performDeposit(String userId, DepositRequestDTO dto) { // Changed userId to String
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setUserId(userId);
        transaction.setSourceUserId(null);
        transaction.setDestinationUserId(userId);
        transaction.setSourceAccountId(null);
        transaction.setDestinationAccountId(dto.getDestinationAccountId());

        assignCategory(dto.getCategoryId(), transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Deposit transaction {} created for user {}", savedTransaction.getTransactionId(), userId);

        publishBalanceUpdateEvent(savedTransaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO performWithdrawal(String userId, WithdrawalRequestDTO dto) { // Changed userId to String
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setUserId(userId);
        transaction.setSourceUserId(userId);
        transaction.setDestinationUserId(null);
        transaction.setSourceAccountId(dto.getSourceAccountId());
        transaction.setDestinationAccountId(null);

        assignCategory(dto.getCategoryId(), transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Withdrawal transaction {} created for user {}", savedTransaction.getTransactionId(), userId);

        publishBalanceUpdateEvent(savedTransaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO performTransfer(String senderUserId, TransferRequestDTO dto) { // Changed senderUserId to String
        // Here, we assume `destinationAccountId` is enough for `user_service` to find the recipient user.
        // If `destinationUserId` is needed explicitly, it would have to be looked up or passed in.
        // For simplicity, `destinationUserId` in the transaction record might be left null if not easily available,
        // or a call to bank_accounts_service could fetch it (adds coupling).

        Transaction sentTransaction = new Transaction();
        sentTransaction.setDate(LocalDateTime.now());
        sentTransaction.setType(TransactionType.TRANSFER_SENT);
        sentTransaction.setAmount(dto.getAmount());
        sentTransaction.setDescription(dto.getDescription());
        sentTransaction.setUserId(senderUserId);
        sentTransaction.setSourceUserId(senderUserId);
        // sentTransaction.setDestinationUserId(TARGET_USER_ID_IF_KNOWN); // This is the tricky part.
        sentTransaction.setSourceAccountId(dto.getSourceAccountId());
        sentTransaction.setDestinationAccountId(dto.getDestinationAccountId());
        assignCategory(dto.getCategoryId(), sentTransaction);
        Transaction savedSentTransaction = transactionRepository.save(sentTransaction);
        log.info("TRANSFER_SENT transaction {} created for sender user {}", savedSentTransaction.getTransactionId(), senderUserId);

        // We must also create a TRANSFER_RECEIVED transaction for the recipient.
        // This requires knowing the recipient's userId. For now, let's assume bank_accounts_service
        // can provide this, or user_service handles the dual nature of the event.
        // To simplify, we will send *two* events, one for sender (debit), one for receiver (credit).
        // The `userId` in the event will be the one whose balance is affected.

        // Event for sender's debit
        publishBalanceUpdateEvent(savedSentTransaction);


        // Create and publish event for recipient's credit.
        // This part is problematic if `destinationUserId` is not known by `transactions_service`.
        // A more robust solution would involve `user_service` reacting to a single `TRANSFER_INITIATED` event
        // and handling both debit and credit, possibly querying `bank_accounts_service` to map accountId to userId.
        // Or, `transactions_service` calls `bank_accounts_service` to get `destinationUserId`.
        // For now, let's assume `user_service` can infer the recipient from `destinationAccountId` in the event
        // OR that the system expects a separate event for the recipient if known.
        // The current BalanceUpdateEventDTO is generic enough that if `user_service` gets an event
        // with type TRANSFER_SENT and a destinationAccountId, it *could* try to credit that account's owner.
        // However, for clarity and explicitness, let's consider if `transactions_service` should try to find
        // the `destinationUserId`. This adds coupling.
        // A cleaner approach: Transaction service creates two transaction records (SENT and RECEIVED) if it can determine both user IDs.
        // If not, it creates SENT, and `user_service` handles the credit to destination based on `destinationAccountId`.

        // Let's stick to only creating the sender's transaction and event for now for simplicity,
        // and assume `user_service` is smart enough to credit the destination account based on
        // the `TRANSFER_SENT` event's `destinationAccountId` field.
        // The alternative is to have `TransferRequestDTO` include `destinationUserId` (obtained by frontend from bank_accounts_service).
        // Let's modify `TransferRequestDTO` to include `destinationUserId` (string).

        // If destinationUserId was in TransferRequestDTO as String destinationUserKeycloakId:
        /*
        if (dto.getDestinationUserKeycloakId() != null) {
             Transaction receivedTransaction = new Transaction();
             receivedTransaction.setDate(LocalDateTime.now());
             receivedTransaction.setType(TransactionType.TRANSFER_RECEIVED);
             receivedTransaction.setAmount(dto.getAmount());
             receivedTransaction.setDescription("Received: " + (dto.getDescription() != null ? dto.getDescription() : "Transfer"));
             receivedTransaction.setUserId(dto.getDestinationUserKeycloakId()); // Recipient user ID
             receivedTransaction.setSourceUserId(senderUserId);
             receivedTransaction.setDestinationUserId(dto.getDestinationUserKeycloakId());
             receivedTransaction.setSourceAccountId(dto.getSourceAccountId());
             receivedTransaction.setDestinationAccountId(dto.getDestinationAccountId());
             // Category for recipient is usually "Income" or not set from sender's choice
             // assignCategory(null, receivedTransaction); // Or a default "Transfer Income" category
             receivedTransaction.setRelatedTransactionId(savedSentTransaction.getTransactionId());
             Transaction savedReceivedTransaction = transactionRepository.save(receivedTransaction);
             savedSentTransaction.setRelatedTransactionId(savedReceivedTransaction.getTransactionId());
             transactionRepository.save(savedSentTransaction); // Link them

             publishBalanceUpdateEvent(savedReceivedTransaction); // Event for receiver
        }
        */
        // For now, keeping it simpler: only one transaction record and one event.
        // `user_service` will need to handle the debit to sender and credit to recipient from one event.

        return transactionMapper.toDto(savedSentTransaction);
    }

    private void assignCategory(Long categoryId, Transaction transaction) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
            transaction.setCategory(category);
        }
    }

    private void publishBalanceUpdateEvent(Transaction transaction) {
        try {
            BalanceUpdateEventDTO event = BalanceUpdateEventDTO.builder()
                    .transactionId(transaction.getTransactionId())
                    .userId(transaction.getUserId())
                    .accountId( (transaction.getType() == TransactionType.DEPOSIT || transaction.getType() == TransactionType.TRANSFER_RECEIVED) ?
                            transaction.getDestinationAccountId() : transaction.getSourceAccountId() )
                    .amount(transaction.getAmount())
                    .transactionType(transaction.getType())
                    .transactionDate(transaction.getDate())
                    .description(transaction.getDescription())
                    .categoryId(transaction.getCategory() != null ? transaction.getCategory().getCategoryId() : null)
                    .build();
            kafkaProducerService.sendBalanceUpdateEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish balance update event for transaction {}: {}", transaction.getTransactionId(), e.getMessage(), e);
            throw new TransactionProcessingException("Failed to publish balance update event", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getTransactionsForUser(String userId, TransactionFilterDTO filters, Pageable pageable) { // Changed
        Specification<Transaction> spec = buildSpecification(userId, filters);
        return transactionRepository.findAll(spec, pageable).map(transactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByIdForUser(Long transactionId, String userId) { // Changed
        Transaction transaction = transactionRepository.findByTransactionIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId + " for user " + userId));
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getAllTransactionsAdmin(TransactionFilterDTO filters, Pageable pageable) {
        Specification<Transaction> spec = buildSpecification(null, filters); // null userId for admin
        return transactionRepository.findAll(spec, pageable).map(transactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByIdAdmin(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByUserIdAndDateRange(String queryUserId, LocalDate startDate, LocalDate endDate) { // Changed
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), queryUserId));
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDateTime));
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDateTime));
            query.orderBy(cb.desc(root.get("date")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return transactionRepository.findAll(spec).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDateTime));
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDateTime));
            query.orderBy(cb.desc(root.get("date")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return transactionRepository.findAll(spec).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    private Specification<Transaction> buildSpecification(String userId, TransactionFilterDTO filters) { // Changed
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null && !userId.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }
            if (filters.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), filters.getType()));
            }
            if (filters.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"), filters.getCategoryId()));
            }
            if (filters.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), filters.getStartDate().atStartOfDay()));
            }
            if (filters.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), filters.getEndDate().atTime(LocalTime.MAX)));
            }
            query.orderBy(criteriaBuilder.desc(root.get("date")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}