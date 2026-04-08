package dev.dharam.cartservice.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Microservices mein Stateless JWT ke liye disable rakhte hain
                .authorizeHttpRequests(auth -> auth
                        // Yahan hum sirf authentication check kar rahe hain
                        // Permissions hum Controller mein @PreAuthorize se handle karenge (Zyada flexible hai)
                        .requestMatchers("/api/v1/cart/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    /**
     * Ye sabse important part hai!
     * Ye AuthService ke "roles" claim ko Spring Security ke "Authorities" mein convert karta hai.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // 1. Aapne AuthService mein claim ka naam "roles" rakha hai, isliye yahan bhi "roles" hoga
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        // 2. Agar aapke DB/Token mein roles "ROLE_CUSTOMER" format mein hain, toh yahan prefix khali rakho.
        // Agar sirf "CUSTOMER" aata hai, toh yahan "ROLE_" likho.
        // Aapke AuthService ke hisaab se hum prefix empty rakh rahe hain.
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
