package dev.dharam.notificationservice.kafka.consumer;

import dev.dharam.notificationservice.kafka.dto.UserRegisteredEvent;
import dev.dharam.notificationservice.kafka.topics.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserRegistrationConsumer {

    @KafkaListener(
            topics = KafkaTopics.USER_REGISTRATION,
            groupId ="email-notification-group",
            containerFactory ="kafkaListenerContainerFactory"

    )
    public void consume(UserRegisteredEvent event){
        log.info("********** NEW NOTIFICATION RECEIVED **********");
        log.info("User ID: {}", event.userId());
        log.info("Sending Welcome Email to: {}", event.email());
        log.info("Message Content: {}", event.welcomeMessage());

        // Yahan hum future mein Java Mail Sender (SMTP) integrate karenge
        log.info("Email successfully sent! (Mocked)");
        log.info("***********************************************");
    }
}
