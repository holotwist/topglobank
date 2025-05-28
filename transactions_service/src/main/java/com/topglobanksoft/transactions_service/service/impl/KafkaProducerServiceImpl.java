package com.topglobanksoft.transactions_service.service.impl;

import com.topglobanksoft.transactions_service.dto.event.BalanceUpdateEventDTO;
import com.topglobanksoft.transactions_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Service for producing balance update events to Kafka
 */
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaTemplate<String, BalanceUpdateEventDTO> kafkaTemplate;

    @Value("${app.kafka.topic.balance-update}") // Injects the topic name
    private String balanceUpdateTopic;
    /**
     * Sends balance update event to Kafka
     * @param event Balance update data to send
     */
    @Override
    public void sendBalanceUpdateEvent(BalanceUpdateEventDTO event) {
        try {
            log.info("Enviando evento de actualización de saldo al topic {}: {}", balanceUpdateTopic, event);
            // Using transactionId or userId as key can help with partitioning in Kafka
            kafkaTemplate.send(balanceUpdateTopic, event.getTransactionId().toString(), event);
        } catch (Exception e) {
            log.error("Error al enviar evento Kafka para transacción {}: {}", event.getTransactionId(), e.getMessage());
            // Implement retry logic or fault handling if required
        }
    }
}