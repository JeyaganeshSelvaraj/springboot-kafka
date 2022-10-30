package com.example.kafkaexample.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class ResponseLogHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLogHandler.class);

    public ServerResponse handle(ServerRequest serverRequest, ServerResponse serverResponse) {
        String requestId = (String) serverRequest.attribute("request_id")
                .orElse("NO REQ ID");
        LOGGER.info("Req ID: {}, Response status {}", requestId,
                serverResponse.statusCode());
        return serverResponse;
    }
}
