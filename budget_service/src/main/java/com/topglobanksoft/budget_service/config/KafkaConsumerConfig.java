package com.topglobanksoft.budget_service.config;

import com.topglobanksoft.budget_service.dto.event.BalanceUpdateEventDTO; // Changed DTO
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

//Class to configurate Kaftka
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    //Injects the value of an external property
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    //Injects the value of an external property
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    //Creates and configurate a @Bean type ConsumerFactory<String, BalanceUpdateEventDTO> to Kaftka
    @Bean
    public ConsumerFactory<String, BalanceUpdateEventDTO> budgetEventConsumerFactory() { // Renamed method for clarity
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        //Creates a JSON deserializer to convert the received messages into Java objects BalanceUpdateEventDTO type
        JsonDeserializer<BalanceUpdateEventDTO> jsonDeserializer = new JsonDeserializer<>(BalanceUpdateEventDTO.class);
        jsonDeserializer.setRemoveTypeHeaders(false);
        jsonDeserializer.addTrustedPackages(
                "com.topglobanksoft.budget_service.dto.event",
                "com.topglobanksoft.transactions_service.dto.event", // Important if producer sends its package in type header
                "com.topglobanksoft.user_service.dto.event" // If user_service were to also produce compatible events
        );

        //Handles an error in a controlled way, allowing the application to continue functioning
        jsonDeserializer.setUseTypeMapperForKey(true);

        ErrorHandlingDeserializer<BalanceUpdateEventDTO> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingDeserializer
        );
    }

    //Configures and return ConcurrentKafkaListenerContainerFactory to create containers who listen Kaftka messages concurrently
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BalanceUpdateEventDTO> budgetKafkaListenerContainerFactory() { // Renamed method
        ConcurrentKafkaListenerContainerFactory<String, BalanceUpdateEventDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(budgetEventConsumerFactory());
        // factory.setConcurrency(3);
        // Error handling (e.g., DLT) can be configured here
        return factory;
    }
}