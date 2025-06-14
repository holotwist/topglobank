package com.topglobanksoft.transactions_service.config;

import com.topglobanksoft.transactions_service.dto.event.BalanceUpdateEventDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
/**
 * Configuration for Kafka producer settings
 */
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    /**
     * Creates producer factory for balance update events
     */
    @Bean
    public ProducerFactory<String, BalanceUpdateEventDTO> balanceUpdateEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Add custom properties for JsonSerializer if needed, e.g., trusted packages
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // Usually false for DTOs if consumer knows type
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    /**
     * Creates Kafka template for balance update events
     */
    @Bean
    public KafkaTemplate<String, BalanceUpdateEventDTO> balanceUpdateEventKafkaTemplate() {
        return new KafkaTemplate<>(balanceUpdateEventProducerFactory());
    }
}