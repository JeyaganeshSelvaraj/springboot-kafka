package com.example.kafkaexample.connector;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CustomDeserializerExceptionHandler implements DeserializationExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDeserializerExceptionHandler.class);
    @Override
    public DeserializationHandlerResponse handle(ProcessorContext processorContext,
                                                 ConsumerRecord<byte[], byte[]> consumerRecord, Exception e) {
        LOGGER.info("ConsumerRecord {}", consumerRecord);
        LOGGER.info("ProcessorContext {}. Application ID: {}. Headers: {}", processorContext.appConfigs()
                , processorContext.applicationId(), processorContext.headers());
        LOGGER.error("Unable to deserialize record for key: {}, value: {}.",
                new String(consumerRecord.key()),new String(consumerRecord.value()),e);

        return DeserializationHandlerResponse.CONTINUE;
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
