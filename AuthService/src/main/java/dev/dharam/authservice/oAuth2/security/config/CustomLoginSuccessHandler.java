package dev.dharam.authservice.oAuth2.security.config;

import dev.dharam.authservice.oAuth2.repositories.JpaRegisteredClientRepository;
import dev.dharam.authservice.oAuth2.services.JpaOAuth2AuthorizationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    // Spring ka default handler jo redirect sambhalta hai
    private final SavedRequestAwareAuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String clientId = request.getParameter("client_id");

        // 1. SavedRequest se Client ID nikalna (For OAuth2 Popup flow)
        if (clientId == null || clientId.trim().isEmpty()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object savedReqObj = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                if (savedReqObj instanceof SavedRequest savedRequest) {
                    String[] clientIds = savedRequest.getParameterValues("client_id");
                    if (clientIds != null && clientIds.length > 0) {
                        clientId = clientIds[0];
                    }
                }
            }
        }

        // --- CASE A: DIRECT LOGIN (No Client ID found) ---
        if (clientId == null || clientId.trim().isEmpty()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\": \"Success\", \"message\": \"Direct Login successful for " + authentication.getName() + "\"}");
            response.getWriter().flush();
            return;
        }

        // --- CASE B: OAUTH2 FLOW (Postman Popup) ---
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Registered client not found: " + clientId);
            return;
        }

        // 2. Token Generation (Access + ID Token)
        Instant now = Instant.now();
        String accessToken = generateToken(authentication, "access_token", now);
        String idToken = generateToken(authentication, "id_token", now);
        String refreshToken = UUID.randomUUID().toString();

        // 3. Persist in DB (Zaroori hai refresh flow ke liye)
        OAuth2Authorization authorization = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(authentication.getName())
                // Use AUTHORIZATION_CODE for standard flow compatibility
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .accessToken(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, now, now.plus(Duration.ofHours(2))))
                .refreshToken(new OAuth2RefreshToken(refreshToken, now, now.plus(Duration.ofDays(30))))
                .build();
        authorizationService.save(authorization);

        // 4. Set HttpOnly Cookie for Refresh Token
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // Localhost par false chalta hai, production pe true karna
                .path("/")     // Pura application access kar sake
                .maxAge(Duration.ofDays(30))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 5. IMPORTANT: Redirect to Postman Callback
        // Humein yahan JSON write nahi karna hai.
        // Default handler Postman window ko apne aap close karwa dega aur token exchange karega.
        delegate.onAuthenticationSuccess(request, response, authentication);
    }

    private String generateToken(Authentication authentication, String type, Instant now) {
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("http://auth-service:8080")
                .subject(authentication.getName())
                .issuedAt(now);

        if ("access_token".equals(type)) {
            claimsBuilder.audience(List.of("productService", "orderService","cartService"))
                    .expiresAt(now.plus(Duration.ofHours(2)))
                    .claim("roles", authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        } else {
            claimsBuilder.audience(List.of("ecom-web-client"))
                    .expiresAt(now.plus(Duration.ofHours(1)))
                    .claim("preferred_username", authentication.getName());
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }
}
