package com.commercetools.aggregator.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;

public interface KafkaReader {
    @KafkaListener(topics = "${spring.kafka.template.default-topic}")
    void listen(@Payload String message) throws IOException;
}
