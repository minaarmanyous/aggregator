FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD ./target/aggregator-0.0.1-SNAPSHOT.jar aggregator.jar
ENTRYPOINT ["java","-jar","aggregator.jar"]