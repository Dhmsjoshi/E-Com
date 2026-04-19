package dev.dharam.cartservice.config.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${spring.security.oauth2.client.registration.cart-service-client.client-id}")
    private String clientId;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder, OAuth2AuthorizedClientManager manager) {
         return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .additionalInterceptors((request, body, execution) -> {
                    // 1. Current Security Context se Authentication object nikalo
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    String token= null;
                    // 2. Check karo ki kya user authenticated hai aur token JWT hai
                    if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                        // 3. Token value ko "Bearer <token>" format mein header mein set karo
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue());
                    }else{
                        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                                .withClientRegistrationId(clientId)
                                .principal("cart-service")
                                .build();

                        OAuth2AuthorizedClient auth2AuthorizedClient = manager.authorize(authorizeRequest);
                        token = auth2AuthorizedClient.getAccessToken().getTokenValue();
                    }

                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer "+token);

                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(RestTemplate restTemplate) {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(restTemplate) // Ye Eureka-aware RestTemplate use karega
                .build();
    }
}
