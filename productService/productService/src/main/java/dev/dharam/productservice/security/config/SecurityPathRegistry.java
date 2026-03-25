package dev.dharam.productservice.security.config;

import org.springframework.stereotype.Component;

@Component
public class SecurityPathRegistry {

    public final String[] PUBLIC_GET_URLS = {
            "/products/homepage",
            "/products/*",
            "/products/category/*",
            "/products/search/**",
            "/category",
            "/category/*"
    };

    public final String[] SWAGGER_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

}