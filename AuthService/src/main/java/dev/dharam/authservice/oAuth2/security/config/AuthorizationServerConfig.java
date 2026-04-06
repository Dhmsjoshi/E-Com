package dev.dharam.authservice.oAuth2.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import dev.dharam.authservice.config.security.SecurityPathRegistry;
import dev.dharam.authservice.oAuth2.models.SecurityUser;
import dev.dharam.authservice.oAuth2.models.UserMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity

public class AuthorizationServerConfig {

    private final CustomLoginSuccessHandler successHandler;
    private final SecurityPathRegistry pathRegistry;

    public AuthorizationServerConfig(@Lazy CustomLoginSuccessHandler successHandler,
                                     SecurityPathRegistry pathRegistry) {
        this.successHandler = successHandler;
        this.pathRegistry = pathRegistry;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        //  Default Authorization Server Security apply
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        //  OpenID Connect 1.0 ko enable via configurer
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
        http

                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // Token validation
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        System.out.println("Loading Updated App Security Config...");
        http

                .cors(Customizer.withDefaults())

                // 2. CSRF handling: APIs ke liye disable, login form ke liye enabled
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(pathRegistry.PUBLIC_POST_URLS)
                        .ignoringRequestMatchers("/api/v1/clients/register")
                )

                // 3. Authorization logic using your Registry
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(pathRegistry.SWAGGER_URLS).permitAll()
                        .requestMatchers(pathRegistry.PUBLIC_POST_URLS).permitAll()
                        .requestMatchers("/login", "/error").permitAll()
                        .anyRequest().authenticated()
                )

                // 4. Custom Login Page connection
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )

                // 5. Logout settings
                .logout(logout -> logout
                        .deleteCookies("refresh_token", "JSESSIONID")
                        .logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080")
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public com.fasterxml.jackson.databind.Module authServiceJacksonModule() {
        com.fasterxml.jackson.databind.module.SimpleModule module =
                new com.fasterxml.jackson.databind.module.SimpleModule("AuthServiceModule");

        // Spring Security ko batata hai ki SecurityUser ko serialize/deserialize kiya ja sakta hai
        module.setMixInAnnotation(SecurityUser.class, UserMixin.class);
        return module;
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context) -> {
            // Access Token ke liye
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Authentication principal = context.getPrincipal();
                context.getClaims().claims((claims) -> {
                    Set<String> roles = principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());
                    claims.put("roles", roles);

                    if (principal.getPrincipal() instanceof SecurityUser user) {
                        claims.put("user_id", user.getId());
                        claims.put("email", user.getUsername());
                        claims.put("phone_number", user.getPhoneNumber());
                    }

                    String clientId = context.getRegisteredClient().getClientId();
                    claims.put("aud", List.of(clientId, "productService", "orderService", "cartService", "paymentService"));
                });
            }

            // ID Token ke liye (Jab OIDC flow ya Refresh flow chale)
            if ("id_token".equals(context.getTokenType().getValue())) {
                context.getClaims().claim("auth_method", "form_login");
                // Yahan aap extra identity claims daal sakte ho
            }
        };
    }

//    @Bean
//    public RegisteredClientRepository registeredClientRepository(dev.dharam.authservice.oAuth2.repositories.ClientRepository clientRepository) {
//       return new JpaRegisteredClientRepository(clientRepository);
//    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
