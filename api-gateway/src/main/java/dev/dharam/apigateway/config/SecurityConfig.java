package dev.dharam.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder reactiveJwtDecoder) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges->exchanges
                    //Open auth service paths like login/register
                        .pathMatchers("/api/auth/**").permitAll()
                    //Swagger/OpenAPI Public Routes (Common Paths)
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers("/swagger-ui.html").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                    //Open for Eureka dashboard or static resources
                        .pathMatchers("/eureka/**").permitAll()
                    // Others are authenticated
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2->oauth2
                        .jwt(jwt->jwt.jwtDecoder(reactiveJwtDecoder))
                );
        return http.build();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder lbWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(WebClient.Builder builder) {
           // Use Reactive version and pass  builder
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri)
                .webClient(builder.build())
                .build();
    }

}
