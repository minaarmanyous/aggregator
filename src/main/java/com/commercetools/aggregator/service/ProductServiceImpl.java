package com.commercetools.aggregator.service;

import com.commercetools.aggregator.exception.MissingResourceUUIDException;
import com.commercetools.aggregator.exception.ObsoleteUpdateRequestException;
import com.commercetools.aggregator.model.Product;
import com.commercetools.aggregator.repository.ProductRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.logging.log4j.LogManager.getLogger;

@Service
public class ProductServiceImpl implements ProductService {

    public static final String PRODUCT_UUID_IS_MISSING_MESSAGE = "Product UUID is missing";
    public static final String OUTDATED_REQUEST_MESSAGE = "Request has date older than the one saved in DB" +
            " for product with ID: %1$s";

    ProductRepository productRepository;

    private static Logger log = getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        validateIncomingProduct(product);

        log.debug(String.format("Valid product received: %1$s", product.getUuid()));

        Optional<Product> dbProduct = productRepository.findById(product.getUuid());
        return dbProduct.map(existingProduct -> editExistingProduct(product, existingProduct))
                .orElseGet(() -> {
                    log.debug(String.format("Creating new product: %1$s", product.getUuid()));
                    //set the creation date for the new record
                    product.setCreatedDate(product.getLastUpdated());
                    return productRepository.save(product);
                });
    }

    private void validateIncomingProduct(Product product) {
        if (!Optional.ofNullable(product.getUuid()).isPresent()) {
            log.error(PRODUCT_UUID_IS_MISSING_MESSAGE);
            throw new MissingResourceUUIDException(PRODUCT_UUID_IS_MISSING_MESSAGE);
        }
    }

    private Product editExistingProduct(Product newProduct, Product existingProduct) {
        //if updated date of the saved record is after the kafka record, then we should skip this record
        //as it will carry outdated values
        if (newProduct.getLastUpdated().isBefore(existingProduct.getLastUpdated())) {
            String exceptionMessage = String.format(OUTDATED_REQUEST_MESSAGE, newProduct.getUuid());
            log.error(exceptionMessage);
            throw new ObsoleteUpdateRequestException(exceptionMessage);
        }
        log.debug(String.format("Editing existing product: %1$s", existingProduct.getUuid()));

        //copy creation date from the DB record because the record received by kafka will not have it
        newProduct.setCreatedDate(existingProduct.getCreatedDate());
        return productRepository.save(newProduct);
    }
}
