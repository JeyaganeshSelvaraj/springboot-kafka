package com.example.kafkaexample.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.kafkaexample.model.SaleEvent;

@Component("SaleEventHandler")
@Scope("prototype")
public class SaleEventHandler implements EventHandler<SaleEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaleEventHandler.class);
    @Override
    public void handle(SaleEvent saleEvent) {
        LOGGER.info("Handling a sale event in consumer {}",saleEvent.toString());
    }
}
