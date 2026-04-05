package dev.dharam.authservice.config.kafka.dto;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        String welcomeMessage
) {
    public UserRegisteredEvent{
        if(welcomeMessage == null) welcomeMessage = "Welcome to Shoppingly!";
    }
}
