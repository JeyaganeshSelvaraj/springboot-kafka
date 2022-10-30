package com.example.kafkaexample.connector;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

@Component
public class EventSender<T extends SpecificRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSender.class);
    @Value("${kafka.topic.events}")
    private String eventsTopic;

    @Autowired
    private KafkaTemplate<String, T> kafkaTemplate;

    public Mono<SendResult<String, T>> send(String id, T event) {
        ListenableFuture<SendResult<String, T>> sendRes = kafkaTemplate.send(eventsTopic, id, event);
        return Mono.fromFuture(sendRes.completable()).map(r -> {
            RecordMetadata metadata = r.getRecordMetadata();
            LOGGER.info("Event sent successfully to kafka, topic-partition={}-{} offset={} timestamp={}",
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    metadata.timestamp());
            return r;
        });
    }

}
