package com.commercetools.aggregator.service;

import com.commercetools.aggregator.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.apache.logging.log4j.LogManager.getLogger;

@Service
public class KafkaReaderImpl implements KafkaReader {
    @Autowired
    ProductService productService;

    public static final String PRODUCT_SAVED = "Saving into DB failed for product with ID: %1$s";

    public static final String PRODUCT_SAVE_FAILED = "Saving into DB failed for product with ID: %1$s";

    private static Logger log = getLogger(KafkaReaderImpl.class);

    @Override
    @KafkaListener(topics = "${spring.kafka.template.default-topic}")
    public void listen(@Payload String message) throws IOException {
        log.info("received message=" + message);

        ProductDTO productDTO = new ObjectMapper().readValue(message, ProductDTO.class);

        CompletableFuture.supplyAsync(() -> productService.saveProduct(ProductDTO.toProduct(productDTO)))
                .thenApplyAsync(result -> {
                    log.debug(String.format(PRODUCT_SAVED, productDTO.getUuid()));
                    return "Save succeeded";
                })
                .exceptionally(ex -> {
                    log.error(String.format(PRODUCT_SAVE_FAILED, productDTO.getUuid(), ex));
                    return "Save failed";
                });


    }
}
