package dev.dharam.productservice.security.config;

import org.springframework.stereotype.Component;

@Component
public class SecurityPathRegistry {

    public final String[] PUBLIC_GET_URLS = {
            "/products/homepage",
            "/products/search/**",
            "/products/category/**",
            "/products/{productId:[\\d]+}",
            "/category",
            "/category/{categoryId:[\\d]+}",
            "/v3/api-docs/**",          // Core JSON/YAML documentation
            "/v3/api-docs.yaml",        // Specific YAML file
            "/swagger-ui/**",           // Swagger UI static resources (JS, CSS)
            "/swagger-ui.html",         // Main entry point for Swagger UI
            "/swagger-resources/**",    // Swagger configuration resources
            "/webjars/**",              // Swagger UI uses webjars internally
            "/configuration/ui",        // Security configuration for UI
            "/configuration/security"   // Security configuration
    };

}