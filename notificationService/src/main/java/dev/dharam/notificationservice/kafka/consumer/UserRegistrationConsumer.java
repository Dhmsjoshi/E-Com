package dev.dharam.notificationservice.kafka.consumer;

import dev.dharam.notificationservice.kafka.dto.UserRegisteredEvent;
import dev.dharam.notificationservice.kafka.topics.KafkaTopics;
import dev.dharam.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRegistrationConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = KafkaTopics.USER_REGISTRATION,
            groupId ="email-notification-group",
            containerFactory ="kafkaListenerContainerFactory"

    )
    public void consume(UserRegisteredEvent event){
        log.info("********** NEW NOTIFICATION RECEIVED **********");
        log.info("User ID: {}", event.userId());
        log.info("Sending Welcome Email to: {}", event.email());

        //call to emailService
        try{
            emailService.sendEmail(
                    event.email(),
                    "Welcome to Shoppingly!",
                    event.welcomeMessage()
            );
            log.info("Email successfully sent to {}", event.email());
            log.info("***********************************************");
        }catch (Exception e){
            log.error("Error while sending email {}",e.getMessage() );
        }



    }
}
