package dev.dharam.authservice.oAuth2.services;

import dev.dharam.authservice.oAuth2.dtos.ClientRegistrationDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;


@Service
public class ClientService {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(RegisteredClientRepository registeredClientRepository, PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerClient(ClientRegistrationDto dto) {
        // 1. Duplicate Check
        if (registeredClientRepository.findByClientId(dto.clientId()) != null) {
            throw new RuntimeException("Client with ID " + dto.clientId() + " already exists!");
        }

        // 2. Base Configuration Setup
        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(dto.clientId())
                .clientIdIssuedAt(Instant.now())
                .clientSecret(passwordEncoder.encode(dto.clientSecret()))
                .clientName(dto.clientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .scopes(scopes -> scopes.addAll(dto.scopes()))

                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(2))
                        .build());

        // 3. Conditional Flow Logic
        if (Boolean.TRUE.equals(dto.isInternalService())) {
            // --- INTERNAL SERVICE (M2M Flow) ---
            builder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);

            builder.clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .setting("is-internal-service", true) // Custom Metadata
                    .build());
        } else {
            // --- EXTERNAL CLIENT (User Flow) ---
            if (dto.redirectUris() == null || dto.redirectUris().isEmpty()) {
                throw new RuntimeException("Redirect URIs are mandatory for external (non-internal) clients!");
            }

            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
            builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
            builder.redirectUris(uris -> uris.addAll(dto.redirectUris()));

            builder.clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(true)
                    .setting("is-internal-service", false) // Custom Metadata
                    .build());
        }

        // 4. Save to Repository
        registeredClientRepository.save(builder.build());
    }
}