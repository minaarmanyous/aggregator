package com.commercetools.aggregator.service;

import com.commercetools.aggregator.dto.ProductDTO;
import com.commercetools.aggregator.dto.ReportingDTO;
import com.commercetools.aggregator.model.Product;
import com.commercetools.aggregator.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportingServiceImpl implements ReportingService {

    ProductRepository productRepository;

    @Autowired
    public ReportingServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ReportingDTO getProductsSummary(LocalDateTime date) {
        long numberOfCreatedProducts = productRepository.getCreatedProductsCount(date.truncatedTo(ChronoUnit.DAYS));
        long numberOfUpdateProducts = productRepository.getUpdatedProductsCount(date.truncatedTo(ChronoUnit.DAYS));

        return new ReportingDTO(numberOfCreatedProducts, numberOfUpdateProducts);
    }

    @Override
    public List<ProductDTO> getProductsList(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<Product> productList = productRepository.findAll(pageable).getContent();
        List<ProductDTO> productDTOList = productList.stream().map(ProductDTO::of).collect(Collectors.toList());

        return productDTOList;
    }
}
