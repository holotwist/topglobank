package com.topglobanksoft.user_service.config;

import com.topglobanksoft.user_service.dto.event.BalanceUpdateEventDTO;
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

    // If you have a specific topic for errors (Dead Letter Topic)
    // @Value("${app.kafka.dlt.balance-update-errors}")
    // private String dltTopic;

    //Creates and configurate a @Bean type ConsumerFactory<String, BalanceUpdateEventDTO> to Kaftka
    @Bean
    public ConsumerFactory<String, BalanceUpdateEventDTO> balanceUpdateEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //Creates a JSON deserializer to convert the received messages into Java objects BalanceUpdateEventDTO type
        JsonDeserializer<BalanceUpdateEventDTO> jsonDeserializer = new JsonDeserializer<>(BalanceUpdateEventDTO.class);
        jsonDeserializer.setRemoveTypeHeaders(false);
        // Trust packages for deserialization. Add package from transactions_service if it's different
        // and producer sends type headers. For now, assuming DTO is local or producer doesn't send type headers.
        jsonDeserializer.addTrustedPackages("com.topglobanksoft.user_service.dto.event", "com.topglobanksoft.transactions_service.dto.event");
        jsonDeserializer.setUseTypeMapperForKey(true);

        ErrorHandlingDeserializer<BalanceUpdateEventDTO> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingDeserializer
        );
    }

    //Defines a Bean that configures ConcurrentKafkaListenerContainerFactory specialiced in consume Kafka messages
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BalanceUpdateEventDTO> balanceUpdateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BalanceUpdateEventDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(balanceUpdateEventConsumerFactory());
        // factory.setConcurrency(3);

        // Example: Configure DeadLetterPublishingRecoverer if you have a DLT
        // DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        //    new DeadLetterPublishingRecoverer(kafkaTemplateForDlt, (record, exception) -> new TopicPartition(dltTopic, -1)),
        //    new FixedBackOff(1000L, 2) // Retry twice with 1s interval
        // );
        // factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}