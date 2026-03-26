package dev.dharam.authservice.config.security;

import dev.dharam.authservice.security.filter.JwtTokenValidationFilter;
import dev.dharam.authservice.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthSecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtUtil jwtUtil;
    private final SecurityPathRegistry pathRegistry;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        // Swagger paths
                        .requestMatchers(pathRegistry.SWAGGER_URLS).permitAll()
                        // Public POST requests (Login/Signup)
                        .requestMatchers(org.springframework.http.HttpMethod.POST, pathRegistry.PUBLIC_POST_URLS).permitAll()
                        // Rest all secured
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenValidationFilter(jwtUtil), BasicAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));

        // 2.  methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 3. Cookies
        configuration.setAllowCredentials(true);

        // 4. Headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));

        // 5. For browser
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); //All APIs
        return source;
    }



}
