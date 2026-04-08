package dev.dharam.paymentservice.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Webhook aur Success Page ko bina token ke allow karo
                        .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/success-page").permitAll()

                        // 2. Swagger/Docs (Optional)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 3. Baaki saare endpoints (like initiate payment) ke liye TOKEN HONA CHAHIYE
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }


        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

            grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
            return jwtAuthenticationConverter;
        }




}
