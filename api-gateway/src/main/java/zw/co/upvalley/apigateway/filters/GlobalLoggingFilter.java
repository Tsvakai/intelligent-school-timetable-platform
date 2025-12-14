package zw.co.upvalley.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Instant startTime = Instant.now();

        // Log request details
        log.info("Incoming request: {} {} from {}",
                request.getMethod(),
                request.getPath(),
                request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "unknown");

        // Add request ID for tracing
        String requestId = java.util.UUID.randomUUID().toString();
        ServerHttpRequest.Builder requestBuilder = request.mutate()
                .header("X-Request-ID", requestId);

        // Safely add X-Forwarded-For header
        if (request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null) {
            requestBuilder.header("X-Forwarded-For", request.getRemoteAddress().getAddress().getHostAddress());
        }

        ServerHttpRequest modifiedRequest = requestBuilder.build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doOnSuccess(v -> {
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);
                    log.info("Request {} {} completed in {}ms with status {}",
                            request.getMethod(),
                            request.getPath(),
                            duration.toMillis(),
                            exchange.getResponse().getStatusCode());
                })
                .doOnError(throwable -> {
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);
                    log.error("Request {} {} failed in {}ms: {}",
                            request.getMethod(),
                            request.getPath(),
                            duration.toMillis(),
                            throwable.getMessage() != null ? throwable.getMessage() : "Unknown error");
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}