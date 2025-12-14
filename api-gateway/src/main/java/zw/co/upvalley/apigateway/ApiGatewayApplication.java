package zw.co.upvalley.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWebFlux
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry circuitBreakerRegistry() {
        return io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry.ofDefaults();
    }
}