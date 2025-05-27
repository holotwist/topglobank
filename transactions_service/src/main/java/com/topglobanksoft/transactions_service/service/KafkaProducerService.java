package com.topglobanksoft.transactions_service.service;

import com.topglobanksoft.transactions_service.dto.event.BalanceUpdateEventDTO;

public interface KafkaProducerService {
    void sendBalanceUpdateEvent(BalanceUpdateEventDTO event);
}