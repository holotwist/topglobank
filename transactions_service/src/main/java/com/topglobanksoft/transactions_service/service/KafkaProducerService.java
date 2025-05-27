package com.topglobanksoft.transactions_service.service;

import com.topglobanksoft.transactions_service.dto.event.BalanceUpdateEventDTO;
/**
 * Service for producing balance update events to Kafka
 */
public interface KafkaProducerService {
    void sendBalanceUpdateEvent(BalanceUpdateEventDTO event);
}