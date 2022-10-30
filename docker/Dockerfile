FROM openjdk:17-oracle as builder
ARG APP_DIR=/app
ARG APP_USER=appuser
WORKDIR ${APP_DIR}
RUN useradd -ms /bin/bash ${APP_USER}
ADD target/*.tar.gz ./
RUN java -Djarmode=layertools -jar lib/*.jar extract

FROM openjdk:17-oracle
ARG APP_DIR=/app
ARG APP_USER=appuser
ENV SERVER_PORT=5454
ENV SERVER_HOST=""
ENV PROFILE=dev
ENV XMS=4096m
ENV XMX=4096m
ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9092
ENV KAFKA_TOPIC_EVENTS=sale_events
ENV KAFKA_SCHEMA_REGISTRY_URL=http://schema-registry:8081
EXPOSE "${SERVER_PORT}"
COPY --from=builder /etc/passwd /etc/passwd
COPY --from=builder /etc/group /etc/group
WORKDIR ${APP_DIR}
USER ${APP_USER}:${APP_USER}
COPY --chown=${APP_USER}:${APP_USER} --from=builder ${APP_DIR}/snapshot-dependencies ./
COPY --chown=${APP_USER}:${APP_USER} --from=builder ${APP_DIR}/spring-boot-loader ./
COPY --chown=${APP_USER}:${APP_USER} --from=builder ${APP_DIR}/application ./
COPY --chown=${APP_USER}:${APP_USER} --from=builder ${APP_DIR}/dependencies ./
COPY --chown=${APP_USER}:${APP_USER} --from=builder ${APP_DIR}/bin ./
COPY --chown=${APP_USER}:${APP_USER} --from=builder ${APP_DIR}/config ./config
CMD ["/app/kafka-application-start.sh"]