package dev.dharam.productservice.security.config;

import dev.dharam.productservice.security.filter.JwtTokenValidationFilter;
import dev.dharam.productservice.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class ProductServiceSecurityConfig {

    private final JwtUtil jwtUtil;
    private final SecurityPathRegistry securityPathRegistry;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, securityPathRegistry.PUBLIC_GET_URLS).permitAll()//
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenValidationFilter(jwtUtil,securityPathRegistry), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
