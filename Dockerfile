FROM maven:3.6-jdk-8-slim AS builder

COPY ./ /opt/frauddetection
WORKDIR /opt/frauddetection
RUN mvn clean install

FROM flink:1.14-scala_2.12

WORKDIR /opt/flink/bin

COPY --from=builder /opt/frauddetection/target/frauddetection-*.jar /opt/frauddetection.jar