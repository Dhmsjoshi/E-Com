package dev.dharam.authservice.config.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityPathRegistry {

    // 1. Public POST URLs
    public final String[] PUBLIC_POST_URLS = {
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/logout" // Logout ko bhi yahan rakh sakte hain agar token validation filter handle kar raha hai
    };

    // 2. Swagger & Documentation URLs
    public final String[] SWAGGER_URLS = {
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/configuration/ui",
            "/configuration/security"
    };

//    // 3. Public GET URLs
//    public final String[] PUBLIC_GET_URLS = {
//            "/api/roles"
//    };
}
