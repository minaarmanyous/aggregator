package com.commercetools.aggregator;


import com.commercetools.aggregator.controller.ProductController;
import com.commercetools.aggregator.dto.ProductDTO;
import com.commercetools.aggregator.dto.ReportingDTO;
import com.commercetools.aggregator.service.ProductService;
import com.commercetools.aggregator.service.ReportingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @MockBean
    private ReportingService reportingService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void productsList_shouldReturnFoundProducts() throws Exception {
        ProductDTO first = new ProductDTO();
        first.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");

        ProductDTO second = new ProductDTO();
        second.setUuid("fccc13f1-f337-480b-9312-a5bb56bcaa11");


        when(reportingService.getProductsList(0, 2)).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/products?pageIndex=0&pageSize=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid", is(first.getUuid())))
                .andExpect(jsonPath("$[1].uuid", is(second.getUuid()))
                );
    }

    @Test
    public void productsList_itemsLessThanPageSize_shouldReturnAllProducts() throws Exception {
        ProductDTO first = new ProductDTO();
        first.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");

        ProductDTO second = new ProductDTO();
        second.setUuid("fccc13f1-f337-480b-9312-a5bb56bcaa11");


        when(reportingService.getProductsList(0, 4)).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/products?pageIndex=0&pageSize=4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid", is(first.getUuid())))
                .andExpect(jsonPath("$[1].uuid", is(second.getUuid()))
                );
    }

    @Test
    public void productsList_requestedItemsMoreThanLimit_shouldReturnBadRequest() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setUuid("fccc13f1-f337-480b-9305-a5bb56bcaa11");

        when(reportingService.getProductsList(0, 100)).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/products?pageIndex=0;pageSize=100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void productsCount_itemsCreatedToday_shouldReturnCorrectCount() throws Exception {
        ReportingDTO reportingDTO = new ReportingDTO(100, 90);

        when(reportingService.getProductsSummary(any())).thenReturn(reportingDTO);

        mockMvc.perform(get("/products/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("numberOfCreatedProducts", is(100)))
                .andExpect(jsonPath("numberOfUpdatedProducts", is(90))
                );
    }
}