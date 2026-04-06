package dev.dharam.orderservice.config.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .additionalInterceptors((request, body, execution) -> {
                    // 1. Current Security Context se Authentication object nikalo
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    // 2. Check karo ki kya user authenticated hai aur token JWT hai
                    if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                        // 3. Token value ko "Bearer <token>" format mein header mein set karo
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue());
                    }

                    return execution.execute(request, body);
                })
                .build();
    }
}
