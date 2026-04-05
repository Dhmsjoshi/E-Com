package dev.dharam.notificationservice.kafka;


import dev.dharam.notificationservice.kafka.dto.UserRegisteredEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // 1. Basic Kafka props (No JsonDeserializer keys here!)
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "email-notification-group-v5");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 2. JsonDeserializer ko Constructor ke through fully configure karein
        // Pehla param: Target Class (Hamara Record)
        // Dusra param: ObjectMapper (null matlab default use karega)
        JsonDeserializer<UserRegisteredEvent> jsonDeserializer = new JsonDeserializer<>(UserRegisteredEvent.class);

        // Yahan setters use karein (Properties Map mein mat daalna)
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);

        // 3. Sabse Important: Constructor Injection
        // Hum valueDeserializer (jsonDeserializer) ko yahan pass kar rahe hain
        // Isliye props mein VALUE_DESERIALIZER_CLASS_CONFIG ki zaroorat nahi hai
        return new DefaultKafkaConsumerFactory<String, Object>(
                props,
                new StringDeserializer(),
                (JsonDeserializer)jsonDeserializer
        );
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
