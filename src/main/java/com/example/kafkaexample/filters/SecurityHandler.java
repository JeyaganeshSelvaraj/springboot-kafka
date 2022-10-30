package com.example.kafkaexample.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SecurityHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHandler.class);
    @Autowired
    private ResponseLogHandler responseLogFilter;

    public Mono<ServerResponse> filter(ServerRequest serverRequest,
                                       HandlerFunction<ServerResponse> handlerFunction) {
        List<String> authorization = serverRequest.headers().header(HttpHeaders.AUTHORIZATION);
        if (authorization.isEmpty()) {
            Mono<ServerResponse> responseMono = ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
            return responseMono.doOnNext(res -> responseLogFilter.handle(serverRequest, res));
        }
        LOGGER.info("Authorization Header: {}", authorization);
        return handlerFunction.handle(serverRequest);
    }
}
