package dev.dharam.authservice.oAuth2.security.config;

import dev.dharam.authservice.oAuth2.repositories.JpaRegisteredClientRepository;
import dev.dharam.authservice.oAuth2.services.JpaOAuth2AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtEncoder jwtEncoder;
    private final JpaOAuth2AuthorizationService authorizationService;
    private final JpaRegisteredClientRepository registeredClientRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        RegisteredClient registeredClient = registeredClientRepository.findByClientId("ecom-web-client");
        Instant now = Instant.now();

        //  1. Access Token
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer("http://auth-service:8080")
                .subject(authentication.getName())
                .audience(List.of("product-service", "order-service"))
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(2)))
                .claim("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        // 2. ID Token
        // Ye frontend ke liye hai taaki wo user ki identity jaan sake
        JwtClaimsSet idClaims = JwtClaimsSet.builder()
                .issuer("http://auth-service:8080")
                .subject(authentication.getName())
                .audience(List.of("ecom-web-client")) // Frontend client ID
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(1)))
                .claim("preferred_username", authentication.getName())
                // yahan email ya name bhi add kar sakte he agar SecurityUser mein hai
                .build();
        String idToken = jwtEncoder.encode(JwtEncoderParameters.from(idClaims)).getTokenValue();

        // 3. Refresh Token & DB Persistence ---
        String refreshToken = UUID.randomUUID().toString();
        OAuth2Authorization authorization = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(authentication.getName())
                .authorizationGrantType(new AuthorizationGrantType("form_login"))
                .accessToken(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, now, now.plus(Duration.ofHours(2))))
                .refreshToken(new OAuth2RefreshToken(refreshToken, now, now.plus(Duration.ofDays(30))))
                .build();
        authorizationService.save(authorization);

        //  4. HTTP-Only Cookie for Refresh Token
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/oauth2/token")
                .maxAge(Duration.ofDays(30))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        //  5. Final JSON Response (Access + ID Token)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write("{" +
                "\"access_token\": \"" + accessToken + "\"," +
                "\"id_token\": \"" + idToken + "\"," +
                "\"expires_in\": 7200" +
                "}");
        writer.flush();
    }
}
