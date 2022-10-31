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


