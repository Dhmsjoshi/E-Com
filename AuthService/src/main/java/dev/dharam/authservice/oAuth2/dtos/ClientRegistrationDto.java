package dev.dharam.authservice.oAuth2.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
        Set<String> scopes,

        @NotNull(message = "isInternalService flag is required") // Boolean wrapper + NotNull
        Boolean isInternalService
) {}