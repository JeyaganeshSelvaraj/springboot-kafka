# springboot-kafka
An example application of Kafka producer and consumer using spring boot

## Prerequisites
<ul>
<li>Java 17</li>
<li>Maven</li>
<li>Docker</li>
<li>Docker Compose(Optional)</li>
</ul>

## Pull the required docker images

<code>docker pull confluentinc/cp-zookeeper:5.4.10-1-ubi8</code>

<code>docker pull confluentinc/cp-kafka:7.2.2</code>

<code> docker pull confluentinc/cp-schema-registry:5.4.10</code>

<code>docker pull docker.io/library/openjdk:17-oracle</code>  

## Building Application

<code>mvn clean verify</code>

## Testing Application

Execute docker compose from the project directory

<code>docker-compose -f ./docker/docker-compose.yaml up</code>

Once all the services are up & running. Try sending below cURL request

<code>curl  -H 'Content-Type: application/json' -u 'admin:admin' localhost:5454/sale -d '{"itemId":1,"quantity": 0, "saleDate":"20221028T220909.999","unitPrice":23.99,"sellerID":"S123"}'</code>

Below logs will be printed in the docker-compose logs

<pre>
kafkaexample-app_1  | 2022-10-31 00:48:32.587  INFO 9 --- [afka-producer-1] c.e.kafkaexample.connector.EventSender   : Event sent successfully to kafka, topic-partition=sale_events-9 offset=0 timestamp=1667177311859
kafkaexample-app_1  | 2022-10-31 00:48:32.592  INFO 9 --- [afka-producer-1] c.e.k.filters.ResponseLogHandler         : Req ID: 0eba3daf-8a72-436e-bc4a-a8c696dd4502, Response status 202 ACCEPTED
schemaregistry_1    | [2022-10-31 00:48:32,622] INFO 172.22.0.5 - - [31/Oct/2022:00:48:32 +0000] "GET /schemas/ids/1?fetchMaxId=false&subject=sale_events-value HTTP/1.1" 200 497  31 (io.confluent.rest-utils.requests)
kafkaexample-app_1  | 2022-10-31 00:48:32.673  INFO 9 --- [-StreamThread-1] c.e.k.connector.EventConsumer            : 1. Received record for item 1, class com.example.kafkaexample.model.SaleEvent
kafkaexample-app_1  | 2022-10-31 00:48:32.677  INFO 9 --- [-StreamThread-1] c.e.k.handlers.SaleEventHandler          : Handling a sale event in consumer {"itemId": 1, "quantity": 0, "saleDate": "20221028T220909.999", "sellerID": "S123", "unitPrice": 23.99}
</pre>
