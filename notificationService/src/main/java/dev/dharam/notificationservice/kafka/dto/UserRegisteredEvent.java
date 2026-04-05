package dev.dharam.notificationservice.kafka.dto;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        String welcomeMessage
) {};
