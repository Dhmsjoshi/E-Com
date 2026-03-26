package dev.dharam.authservice.oAuth2.models;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "`authorization`", indexes = @Index(columnList = "registeredClientId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Authorization {

    @Id
    private String id; // Spring Security requires String ID here

    @Column(nullable = false)
    private String registeredClientId;

    @Column(nullable = false)
    private String principalName;

    @Column(nullable = false)
    private String authorizationGrantType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(length = 1000)
    private String authorizedScopes;

    @Column(columnDefinition = "TEXT")
    private String attributes;

    @Column(length = 500)
    private String state;

    @Column(columnDefinition = "TEXT")
    private String authorizationCodeValue;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;
    @Column(columnDefinition = "TEXT")
    private String authorizationCodeMetadata;

    @Column(columnDefinition = "TEXT")
    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    @Column(columnDefinition = "TEXT")
    private String accessTokenMetadata;
    private String accessTokenType;
    @Column(length = 1000)
    private String accessTokenScopes;

    @Column(columnDefinition = "TEXT")
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;
    @Column(columnDefinition = "TEXT")
    private String refreshTokenMetadata;

    @Column(columnDefinition = "TEXT")
    private String oidcIdTokenValue;
    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;
    @Column(columnDefinition = "TEXT")
    private String oidcIdTokenMetadata;
    @Column(columnDefinition = "TEXT")
    private String oidcIdTokenClaims;

    @Column(columnDefinition = "TEXT")
    private String userCodeValue;
    private Instant userCodeIssuedAt;
    private Instant userCodeExpiresAt;
    @Column(columnDefinition = "TEXT")
    private String userCodeMetadata;

    @Column(columnDefinition = "TEXT")
    private String deviceCodeValue;
    private Instant deviceCodeIssuedAt;
    private Instant deviceCodeExpiresAt;
    @Column(columnDefinition = "TEXT")
    private String deviceCodeMetadata;
}