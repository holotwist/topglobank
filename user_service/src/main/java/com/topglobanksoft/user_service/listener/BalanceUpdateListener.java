package com.topglobanksoft.user_service.listener;

import com.topglobanksoft.user_service.dto.event.BalanceUpdateEventDTO;
import com.topglobanksoft.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceUpdateListener {

    private final UserService userService;

    @KafkaListener(topics = "${app.kafka.topic.balance-update}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "balanceUpdateKafkaListenerContainerFactory")
    public void handleBalanceUpdateEvent(
            @Payload BalanceUpdateEventDTO event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received balance update event from topic {}: Key={}, Payload={}", topic, key, event);

        if (event.getUserId() == null || event.getUserId().isBlank() || // Check for blank String
                event.getAmount() == null || event.getAmount().signum() <= 0 || event.getTransactionType() == null) {
            log.warn("Balance update event skipped due to missing critical data or non-positive amount: {}", event);
            return;
        }

        try {
            boolean isCredit = switch (event.getTransactionType()) {
                case DEPOSIT, TRANSFER_RECEIVED -> true;
                default -> false;
            };

            userService.updateUserBalance(event.getUserId(), event.getAmount(), isCredit, event.getTransactionType().name());
            log.info("User balance updated successfully for UserId={}, Amount={}, Type={}",
                    event.getUserId(), event.getAmount(), event.getTransactionType());

        } catch (Exception e) {
            log.error("Error processing balance update event for UserId {}: {} - Error: {}",
                    event.getUserId(), event, e.getMessage(), e);
            // Consider rethrowing to let Kafka retry mechanism handle it, or send to DLT
            // throw new RuntimeException("Failed to process balance update event", e);
        }
    }
}