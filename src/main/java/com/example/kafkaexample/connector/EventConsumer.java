package com.example.kafkaexample.connector;


import com.example.kafkaexample.handlers.EventHandler;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@ConditionalOnProperty(
        value = "kafka.consumer.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class EventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
    private static final AtomicInteger TOTAL_RECORDS_CONSUMED = new AtomicInteger(0);
    @Autowired
    private SchemaRegistryClient schemaRegistryClient;
    @Autowired
    private ApplicationContext appCtx;
    @Value("${kafka.schema.registry.url}")
    private String schemaRegistry;
    @Value("${kafka.topic.events}")
    private String eventsTopic;

    @Autowired
    @SuppressWarnings("unchecked")
    <T extends SpecificRecord> void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, T> messageStream = streamsBuilder.stream(eventsTopic, Consumed.with(Serdes.String(), getSpecificAvroSerde()));
        messageStream.foreach((k, v) -> {
            int totalRecordsConsumed = TOTAL_RECORDS_CONSUMED.incrementAndGet();
            LOGGER.info("{}. Received record for item {}, {}", totalRecordsConsumed,
                    k, v.getClass());
            try {                
                appCtx.getBean(v.getClass().getSimpleName()+"Handler", EventHandler.class).handle(v);
            } catch (Throwable t) {
                LOGGER.error("Error while calling event handler", t);
            }
        });
    }

    public int totalRecordsConsumed() {
        return TOTAL_RECORDS_CONSUMED.get();
    }

    private <T extends SpecificRecord> SpecificAvroSerde<T> getSpecificAvroSerde() {
        SpecificAvroSerde<T> valueSerde = new SpecificAvroSerde<>(schemaRegistryClient);
        Map<String, Object> props = new HashMap<>();
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        props.put(KafkaAvroDeserializerConfig.AVRO_USE_LOGICAL_TYPE_CONVERTERS_CONFIG, true);
        valueSerde.configure(props, false);
        return valueSerde;
    }

}
