package dev.dharam.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomTokenRelayFilter extends AbstractGatewayFilterFactory<Object> {
    public CustomTokenRelayFilter() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            // Yahan aap manually Authorization header nikal kar aage bhej sakte ho
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null) {
                exchange.getRequest().mutate().header("Authorization", authHeader).build();
            }
            return chain.filter(exchange);
        };
    }
}
