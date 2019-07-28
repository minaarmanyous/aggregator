package com.commercetools.aggregator.controller;

import com.commercetools.aggregator.dto.ReportingDTO;
import com.commercetools.aggregator.service.ProductService;
import com.commercetools.aggregator.service.ReportingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/products")
@Slf4j
public class ProductController {

    public static final String PAGE_SIZE_ERROR_MESSAGE = "Page size parameter exceeds the allowed value (%1$s)";

    @Autowired
    ProductService productService;

    @Autowired
    ReportingService reportingService;

    @Value("${reporting.product.maxPageSize}")
    private int maxPageSize;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getProducts(@RequestParam(required = false) int pageIndex, @RequestParam(required = false) int pageSize) {
        if (pageSize == 0 || pageSize > maxPageSize) {
            String errorMessage = String.format(PAGE_SIZE_ERROR_MESSAGE, maxPageSize);
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        return ResponseEntity.ok(reportingService.getProductsList(pageIndex, pageSize));
    }

    @GetMapping(value = "/stats", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReportingDTO> getProductsSummary() {
        return ResponseEntity.ok(reportingService.getProductsSummary(LocalDateTime.now()));
    }


}
