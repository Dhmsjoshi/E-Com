package dev.dharam.authservice.oAuth2.models;

import dev.dharam.authservice.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "`client`", uniqueConstraints = @UniqueConstraint(columnNames = "clientId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseModel {

    @Column(nullable = false)
    private String clientId;

    @Column(name = "client_id_issued_at", nullable = false, updatable = false)
    private Instant clientIdIssuedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Column(nullable = false)
    private String clientName;

    @Column(length = 1000, nullable = false)
    private String clientAuthenticationMethods;

    @Column(length = 1000, nullable = false)
    private String authorizationGrantTypes;

    @Column(length = 1000)
    private String redirectUris;

    @Column(length = 1000)
    private String postLogoutRedirectUris;

    @Column(length = 1000, nullable = false)
    private String scopes;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String clientSettings;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String tokenSettings;

    @Builder.Default
    @Column(name = "is_internal_service", nullable = false)
    private boolean isInternalService = false;

}