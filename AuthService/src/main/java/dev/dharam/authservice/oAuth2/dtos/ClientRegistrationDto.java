package dev.dharam.authservice.oAuth2.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record ClientRegistrationDto(
        @NotBlank(message = "ClientId is required")
        String clientId,

        @NotBlank(message = "ClientSecret is required")
        String clientSecret,

        @NotBlank(message = "ClientName is required")
        String clientName,

        @NotEmpty(message = "At least one Redirect URI is required")
        Set<String> redirectUris,

        @NotEmpty(message = "At least one Scope is required")
        Set<String> scopes
) {}