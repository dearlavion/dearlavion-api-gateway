package com.dearlavion.gateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex.getMessage() != null && ex.getMessage().contains("429")) {
            status = HttpStatus.TOO_MANY_REQUESTS;
        }

        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            String json = """
            {
              "status": 429,
              "error": "Too Many Requests",
              "message": "You are sending too many requests. Please slow down."
            }
            """;

            var buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(json.getBytes());

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(
                    org.springframework.http.MediaType.APPLICATION_JSON
            );

            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        return Mono.error(ex);
    }
}
