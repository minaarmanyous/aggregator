package com.commercetools.aggregator.repository;

import com.commercetools.aggregator.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {

    @Query(value = "select count(*) from product where CREATED_DATE >= :fromDate" ,nativeQuery = true)
    public long getCreatedProductsCount(@Param("fromDate") LocalDateTime fromDate);

    @Query(value = "select count(*) from product where LAST_UPDATED >= :fromDate" ,nativeQuery = true)
    public long getUpdatedProductsCount(@Param("fromDate") LocalDateTime fromDate);
}

