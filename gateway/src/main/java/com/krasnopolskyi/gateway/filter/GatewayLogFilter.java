package com.krasnopolskyi.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "GATEWAY")
@Order(1)
public class GatewayLogFilter implements WebFilter {

    private static final String REQUEST_ID_HEADER = "requestId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Get the requestId from the header or generate a new one
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();  // Generate a new requestId if not present
        }

        // Log the incoming request details
        log.info("Incoming Request: RequestId={}, Endpoint={}, Method={}, Params={}",
                requestId, exchange.getRequest().getURI(), exchange.getRequest().getMethod(),
                formatParams(exchange.getRequest().getQueryParams()));

        // Modify the request to include the requestId header
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        // Continue with the request processing, passing the modified request
        String finalRequestId = requestId;
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doOnTerminate(() -> logAfterRequest(exchange, finalRequestId));
    }

    private void logAfterRequest(ServerWebExchange exchange, String requestId) {
        int status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : 0;
        log.info("Request Completed: RequestId={}, Status={}", requestId, status);
    }

    private String formatParams(MultiValueMap<String, String> paramMap) {
        return paramMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(", ", entry.getValue()))
                .collect(Collectors.joining(", "));
    }
}
