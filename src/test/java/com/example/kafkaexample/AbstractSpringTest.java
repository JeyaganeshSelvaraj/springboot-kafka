package com.example.kafkaexample;

import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest(properties = "spring.main.web-application-type=reactive")
@Testcontainers
public class AbstractSpringTest {
    public static final int KAFKA_PORT = 19092;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSpringTest.class);
    private static final Network SHARED_NETWORK = Network.newNetwork();
    private static final int SCHEMA_REGISTRY_PORT = 8081;
    @Container
    static GenericContainer<?> zookeeper = zkContainer();
    @Container
    static GenericContainer<?> kafka = kafkaContainer();
    @Container
    static GenericContainer<?> schemaRegistry = schemaRegistryContainer();


    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", () -> kafka.getHost()+":"+kafka.getMappedPort(KAFKA_PORT));
        registry.add("kafka.schema.registry.url", () -> String.format("http://%s:%d",
                schemaRegistry.getHost(), schemaRegistry.getMappedPort(SCHEMA_REGISTRY_PORT)));
        registry.add("kafka.consumer.enabled",()->"true");
    }

    private static GenericContainer<?> zkContainer() {
        return new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper:5.4.10-1-ubi8"))
                .withExposedPorts(2181)
                .withCreateContainerCmdModifier(cmd ->
                        cmd.withName("zookeeper")
                                .withHostConfig(HostConfig.newHostConfig()
                                        .withNetworkMode(SHARED_NETWORK.getId())
                                        .withPortBindings(PortBinding.parse("12181:2181"))
                                )
                )
                .withLogConsumer(new Slf4jLogConsumer(LOGGER))
                .withNetwork(SHARED_NETWORK).withNetworkMode(SHARED_NETWORK.getId())
                .withNetworkAliases("zookeeper")
                .withEnv("ZOOKEEPER_SERVER_ID", "1")
                .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
                .withEnv("ZOOKEEPER_TICK_TIME", "2000")
                .withEnv("ZOOKEEPER_SERVERS", "zookeeper:22888:23888");
    }

    private static GenericContainer<?> kafkaContainer() {
        return new GenericContainer<>(DockerImageName.parse("confluentinc/cp-kafka:7.2.2"))
                .withCreateContainerCmdModifier(cmd ->
                        cmd.withName("kafka")
                                .withHostConfig(HostConfig.newHostConfig()
                                        .withNetworkMode(SHARED_NETWORK.getId())
                                        .withPortBindings(PortBinding.parse(KAFKA_PORT+":"+KAFKA_PORT))
                                )
                )
                .withNetwork(SHARED_NETWORK).withNetworkMode(SHARED_NETWORK.getId())
                .withNetworkAliases("kafka")
                .withAccessToHost(true)
                .withExposedPorts(KAFKA_PORT)
                .withEnv("KAFKA_SCHEMA_REGISTRY_URL", "schemaregistry:8081")
                .withEnv("KAFKA_ZOOKEEPER_CONNECT", "zookeeper:2181")
                .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT")
                .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
                .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:19092")
                .withEnv("KAFKA_BROKER_ID", "1")
                .withEnv("KAFKA_BROKER_RACK", "r1")
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_DELETE_TOPIC_ENABLE", "true")
                .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
                .withEnv("KAFKA_JMX_PORT", "9991")
                .withLogConsumer(new Slf4jLogConsumer(LOGGER))
                .dependsOn(zookeeper).withStartupTimeout(Duration.ofMinutes(2L));
    }

    private static GenericContainer<?> schemaRegistryContainer() {
        return new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry:5.4.10"))
                .dependsOn(kafka)
                .withCreateContainerCmdModifier(cmd -> cmd.withName("schemaregistry")
                        .withHostConfig(HostConfig.newHostConfig()
                                .withNetworkMode(SHARED_NETWORK.getId())
                                .withPortBindings(PortBinding.parse("18081:8081"))
                        )
                )
                .withExposedPorts(SCHEMA_REGISTRY_PORT)
                .withNetwork(SHARED_NETWORK).withNetworkMode(SHARED_NETWORK.getId())
                .withNetworkAliases("schemaregistry")
                .withAccessToHost(true)
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL", "zookeeper:2181")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schemaregistry")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .withLogConsumer(new Slf4jLogConsumer(LOGGER));
    }

    protected Resource readFile(String fileName) {
        return new ClassPathResource("./requests/" + fileName);
    }
}
