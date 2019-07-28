package com.commercetools.aggregator;

import com.commercetools.aggregator.exception.MissingResourceUUIDException;
import com.commercetools.aggregator.exception.ObsoleteUpdateRequestException;
import com.commercetools.aggregator.model.Product;
import com.commercetools.aggregator.repository.ProductRepository;
import com.commercetools.aggregator.service.ProductService;
import com.commercetools.aggregator.service.ProductServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ProductServiceTests {

    @MockBean
    private ProductRepository productRepository;

    ProductService productService;

    @Before
    public void initializeTests() {
        productService = new ProductServiceImpl(productRepository);
    }

    @Test(expected = MissingResourceUUIDException.class)
    public void saveProduct_MissingID_ExceptionThrown() {
        productService.saveProduct(new Product());
    }

    @Test(expected = ObsoleteUpdateRequestException.class)
    public void saveProduct_obsoleteRequestDate_exceptionThrown() {
        Product consumedProduct = new Product();
        consumedProduct.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");

        Product dbProduct = new Product();
        dbProduct.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");
        dbProduct.setLastUpdated(LocalDateTime.now());

        consumedProduct.setLastUpdated(dbProduct.getLastUpdated().minusSeconds(1));

        when(productRepository.findById(consumedProduct.getUuid())).thenReturn(Optional.of(dbProduct));
        productService.saveProduct(consumedProduct);
        reset(productRepository);

    }

    @Test
    public void saveProduct_validRequest_existingProduct() {

        Product consumedProduct = new Product();
        consumedProduct.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");

        Product dbProduct = new Product();
        dbProduct.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");
        dbProduct.setLastUpdated(LocalDateTime.now());

        consumedProduct.setLastUpdated(dbProduct.getLastUpdated().plusSeconds(1));

        when(productRepository.findById(consumedProduct.getUuid())).thenReturn(Optional.of(dbProduct));
        productService.saveProduct(consumedProduct);
        reset(productRepository);
    }

    @Test
    public void saveProduct_validRequest_newProduct() {
        Product consumedProduct = new Product();
        consumedProduct.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");
        consumedProduct.setLastUpdated(LocalDateTime.now());

        when(productRepository.findById(consumedProduct.getUuid())).thenReturn(Optional.ofNullable(null));
        Product savedProduct = productService.saveProduct(consumedProduct);

        verify(productRepository, VerificationModeFactory.times(1)).save(consumedProduct);
        reset(productRepository);
    }

}
