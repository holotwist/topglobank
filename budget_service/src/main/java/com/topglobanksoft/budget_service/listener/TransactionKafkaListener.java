package com.topglobanksoft.budget_service.listener;

import com.topglobanksoft.budget_service.dto.event.BalanceUpdateEventDTO;
import com.topglobanksoft.budget_service.dto.event.TransactionType;
import com.topglobanksoft.budget_service.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionKafkaListener {

    private final BudgetService budgetService;

    //Defines an static and constant list
    private static final List<TransactionType> EXPENSE_TRANSACTION_TYPES = Arrays.asList(
            TransactionType.WITHDRAWAL,
            TransactionType.TRANSFER_SENT
    );

    //Listens the Kafka messages
    @KafkaListener(topics = "${app.kafka.topic.budget-relevant-transactions}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "budgetKafkaListenerContainerFactory")
    public void handleTransactionDoneEvent(
            @Payload BalanceUpdateEventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received budget-relevant event from topic {}: Key={}, Payload={}", topic, key, event);

        if (event.getUserId() == null || event.getUserId().isBlank() || // Check for blank String userId
                event.getCategoryId() == null || event.getTransactionDate() == null ||
                event.getAmount() == null || event.getAmount().signum() <= 0 ||
                event.getTransactionType() == null ) {
            log.warn("Event skipped due to missing critical data or non-positive amount: {}", event);
            return;
        }

        if (EXPENSE_TRANSACTION_TYPES.contains(event.getTransactionType())) {
            try {
                if (event.getTransactionDate() == null) { // Redundant due to above check but good for clarity
                    log.warn("Event skipped due to null transactionDate: {}", event);
                    return;
                }
                // Using system default zone; consider making this configurable or using UTC
                int month = event.getTransactionDate().atZone(ZoneId.systemDefault()).getMonthValue();
                int year = event.getTransactionDate().atZone(ZoneId.systemDefault()).getYear();

                budgetService.updateBudgetSpentAmount(
                        event.getUserId(), // Now a String
                        event.getCategoryId(),
                        year,
                        month,
                        event.getAmount()
                );
                log.info("Budget spent amount updated successfully for User={}, Category={}, Year={}, Month={}, Amount={}",
                        event.getUserId(), event.getCategoryId(), year, month, event.getAmount());

            } catch (Exception e) {
                log.error("Error processing transaction event to update budget: {} - Error: {}", event, e.getMessage(), e);
            }
        } else {
            log.info("Event for User={}, Category={} is not an expense type for budgeting (Type: {}). Skipping.",
                    event.getUserId(), event.getCategoryId(), event.getTransactionType());
        }
    }
}