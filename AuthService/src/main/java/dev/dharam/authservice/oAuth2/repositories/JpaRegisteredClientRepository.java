package dev.dharam.authservice.oAuth2.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dharam.authservice.oAuth2.models.Client;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

@Component
public class JpaRegisteredClientRepository implements RegisteredClientRepository {
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JpaRegisteredClientRepository(ClientRepository clientRepository) {
        Assert.notNull(clientRepository, "clientRepository cannot be null");
        this.clientRepository = clientRepository;

        ClassLoader classLoader = JpaRegisteredClientRepository.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");

        // 1. Pehle check karo ki ye ClientId pehle se DB mein hai ya nahi
        Optional<Client> existingClient = this.clientRepository.findByClientId(registeredClient.getClientId());

        Client entityToSave;

        if (existingClient.isPresent()) {
            // 2. Agar hai, toh DB wali entity uthao aur usme naye values map karo (Update flow)
            entityToSave = toEntity(registeredClient);
            // Important: Purani ID set karni padegi taaki Hibernate naya record na banaye
            entityToSave.setId(existingClient.get().getId());
            entityToSave.setCreatedAt(existingClient.get().getCreatedAt()); // Purani date preserve karo
        } else {
            // 3. Agar naya hai, toh fresh entity banao (Insert flow)
            entityToSave = toEntity(registeredClient);
            // Ensure karo ki ID null hai taaki Hibernate INSERT chalaye
            entityToSave.setId(null);
        }

        this.clientRepository.saveAndFlush(entityToSave);
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return this.clientRepository.findById(UUID.fromString(id)).map(this::toObject).orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        return this.clientRepository.findByClientId(clientId).map(this::toObject).orElse(null);
    }

    private RegisteredClient toObject(Client client) {
        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(
                client.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = StringUtils.commaDelimitedListToSet(
                client.getAuthorizationGrantTypes());
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(
                client.getRedirectUris());
        Set<String> postLogoutRedirectUris = StringUtils.commaDelimitedListToSet(
                client.getPostLogoutRedirectUris());
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(
                client.getScopes());

        RegisteredClient.Builder builder = RegisteredClient.withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientIdIssuedAt(client.getClientIdIssuedAt())
                .clientSecret(client.getClientSecret())
                .clientSecretExpiresAt(client.getClientSecretExpiresAt())
                .clientName(client.getClientName())
                .clientAuthenticationMethods(authenticationMethods ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))
                .authorizationGrantTypes((grantTypes) ->
                        authorizationGrantTypes.forEach(grantType ->
                                grantTypes.add(resolveAuthorizationGrantType(grantType))))
                .redirectUris((uris) -> uris.addAll(redirectUris))
                .postLogoutRedirectUris((uris) -> uris.addAll(postLogoutRedirectUris))
                .scopes((scopes) -> scopes.addAll(clientScopes));

        // Client Settings: Copying to a new HashMap to avoid UnsupportedOperationException
        Map<String, Object> clientSettingsMap = new HashMap<>(parseMap(client.getClientSettings()));
        clientSettingsMap.put("is-internal-service", client.isInternalService());
        builder.clientSettings(ClientSettings.withSettings(clientSettingsMap).build());

        // Token Settings: Also copying to a new HashMap for consistency and safety
        Map<String, Object> tokenSettingsMap = new HashMap<>(parseMap(client.getTokenSettings()));
        builder.tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build());

        return builder.build();
    }

    private Client toEntity(RegisteredClient registeredClient) {
        List<String> clientAuthenticationMethods = new ArrayList<>(registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(method ->
                clientAuthenticationMethods.add(method.getValue()));

        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes().forEach(type ->
                authorizationGrantTypes.add(type.getValue()));

        Client entity = new Client();

        // ID yahan set NAHI karenge, wo save() method handle karega

        entity.setDeleted(false);
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        Boolean isInternal = registeredClient.getClientSettings().getSetting("is-internal-service");
        entity.setInternalService(Boolean.TRUE.equals(isInternal));

        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt() != null ?
                registeredClient.getClientIdIssuedAt() : now);

        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt());
        entity.setClientName(registeredClient.getClientName());

        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        entity.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        entity.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        entity.setPostLogoutRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getPostLogoutRedirectUris()));
        entity.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));

        entity.setClientSettings(writeMap(registeredClient.getClientSettings().getSettings()));
        entity.setTokenSettings(writeMap(registeredClient.getTokenSettings().getSettings()));

        return entity;
    }

    private Map<String, Object> parseMap(String data) {
        try {
            if (!StringUtils.hasText(data)) return new HashMap<>();
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(authorizationGrantType);              // Custom authorization grant type
    }

    private static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);      // Custom client authentication method
    }
}
