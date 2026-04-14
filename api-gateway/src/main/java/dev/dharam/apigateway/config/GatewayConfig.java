package dev.dharam.apigateway.config;

import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
     //Is bean ko register karne se Gateway ko TokenRelay factory mil jayegi
//    @Bean
//    public TokenRelayGatewayFilterFactory tokenRelayGatewayFilterFactory(
//            ReactiveClientRegistrationRepository clientRegistrationRepository) {
//        // TokenRelay ko Spring ke client repository ki zaroorat hoti hai
//        return new TokenRelayGatewayFilterFactory(clientRegistrationRepository);
//    }
}
