package com.example.kafkaexample.config;


import com.example.kafkaexample.connector.CustomDeserializerExceptionHandler;
import com.example.kafkaexample.model.SaleEvent;

import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafkaStreams
@ConditionalOnProperty(
        value = "kafka.consumer.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class KafkaStreamConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamConfig.class);
    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${kafka.schema.registry.url}")
    private String schemaRegistry;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "kafka-consumer");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, CustomDeserializerExceptionHandler.class);
        return new KafkaStreamsConfiguration(props);
    }


    @Bean
    SchemaRegistryClient schemaRegistryClient() {
        SchemaRegistryClient schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistry, 1000);
        try {
            schemaRegistryClient
                    .register(SaleEvent.getClassSchema().getFullName(),
                            new AvroSchema(SaleEvent.getClassSchema()));
        } catch (IOException | RestClientException e) {
            LOGGER.warn("Error while registering schema ", e);
        }
        return schemaRegistryClient;
    }
}