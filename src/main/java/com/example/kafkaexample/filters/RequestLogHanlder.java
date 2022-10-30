package com.example.kafkaexample.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.UUID;

@Component
public class RequestLogHanlder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogHanlder.class);
    public ServerRequest handle(ServerRequest serverRequest) {
        String requesId = UUID.randomUUID().toString();
        serverRequest.attributes().put("request_id",requesId);
        LOGGER.info("Request Path: {}",serverRequest.path());
        return serverRequest;
    }

}
