package com.example.kafkaexample;

import org.junit.jupiter.api.Test;

import com.example.kafkaexample.connector.EventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Objects;
import java.util.stream.IntStream;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@AutoConfigureWebTestClient
class KafkaexampleApplicationTests extends AbstractSpringTest {

    @Autowired
    private WebTestClient webClient;
    @Autowired(required = false)
    private EventConsumer consumer;

    @Test
    void contextLoads() {

    }

    @Test
    void testKafkaProducer() {
        Resource resource = readFile("saleevent.json");
        IntStream.range(0, 100).forEach(i -> webClient.post()
                .uri("/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzCg==")
                .body(BodyInserters.fromResource(resource))
                .exchange()
                .expectStatus().isAccepted());
        if (Objects.nonNull(consumer)) {
            await().until(() -> consumer.totalRecordsConsumed() == 100);
        }
    }
}
