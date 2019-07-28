package com.commercetools.aggregator;

import com.commercetools.aggregator.dto.ProductDTO;
import com.commercetools.aggregator.dto.ReportingDTO;
import com.commercetools.aggregator.model.Product;
import com.commercetools.aggregator.repository.ProductRepository;
import com.commercetools.aggregator.service.ReportingService;
import com.commercetools.aggregator.service.ReportingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ReportingServiceTests {

    public static final long NUMBER_OF_CREATED_PRODUCTS = 100L;
    public static final long NUMBER_OF_UPDATED_PRODUCTS = 90L;

    @MockBean
    private ProductRepository productRepository;

    ReportingService reportingService;

    @Before
    public void initializeTests() {
        reportingService = new ReportingServiceImpl(productRepository) {
        };
    }

    @Test
    public void productsSummary_get_test() {

        when(productRepository.getCreatedProductsCount(any())).thenReturn(NUMBER_OF_CREATED_PRODUCTS);
        when(productRepository.getUpdatedProductsCount(any())).thenReturn(NUMBER_OF_UPDATED_PRODUCTS);

        ReportingDTO reportingDTO = reportingService.getProductsSummary(LocalDateTime.now());

        verify(productRepository, VerificationModeFactory.times(1)).getCreatedProductsCount(any());
        verify(productRepository, VerificationModeFactory.times(1)).getUpdatedProductsCount(any());

        assert (reportingDTO.getNumberOfCreatedProducts() == NUMBER_OF_CREATED_PRODUCTS);
        assert (reportingDTO.getNumberOfUpdatedProducts() == NUMBER_OF_UPDATED_PRODUCTS);
    }

    @Test
    public void productList_get_test() {

        List<Product> productList = Arrays.asList(new Product(), new Product(), new Product());

        Page<Product> productsPage = Mockito.mock(Page.class);

        when(productsPage.getContent()).thenReturn(productList);
        when(productRepository.findAll((Pageable) any())).thenReturn(productsPage);

        List<ProductDTO> productDTOS = reportingService.getProductsList(0, 3);

        verify(productRepository, VerificationModeFactory.times(1)).findAll((Pageable) any());

        assert (productDTOS.size() == 3);
    }

}
