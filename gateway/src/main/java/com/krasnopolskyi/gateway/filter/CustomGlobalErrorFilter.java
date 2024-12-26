//package com.krasnopolskyi.gateway.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class CustomGlobalErrorFilter implements GlobalFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return chain.filter(exchange).onErrorResume(ex -> {
//            // Get the response object to modify it
//            ServerHttpResponse response = exchange.getResponse();
//
//            // Customize the status code based on the error or default to 500
//            response.setStatusCode(HttpStatus.NOT_FOUND);
//
//            // Custom error message you want to show
//            String errorMessage = "{\"error\": \"Requested resource not found\"}";
//            byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bytes);
//
//            // Set response headers and content type
//            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//
//            // Write the custom error message
//            return response.writeWith(Mono.just(buffer));
//        });
//    }
//}
