package com.example.kafkaexample.controllers;

import com.example.kafkaexample.connector.EventSender;
import com.example.kafkaexample.model.SaleEvent;

import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class SalesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesService.class);

    @Autowired
    private EventSender<SpecificRecord> sender;

    public Mono<ServerResponse> createNewSale(ServerRequest serverRequest) {
        LOGGER.info("Received a sale call");
        Mono<SaleEvent> eventMono = serverRequest.bodyToMono(SaleEvent.class);
        return eventMono
                .flatMap(saleEvent -> sender.send(Integer.toString(saleEvent.getItemId()), saleEvent))
                .doOnError(e -> LOGGER.error("Failed to send sale event.", e))
                .flatMap(res -> ServerResponse.accepted().contentType(MediaType.APPLICATION_JSON).build()
                );
    }
}
