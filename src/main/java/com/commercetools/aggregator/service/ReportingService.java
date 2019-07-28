package com.commercetools.aggregator.service;

import com.commercetools.aggregator.dto.ProductDTO;
import com.commercetools.aggregator.dto.ReportingDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingService {

    public List<ProductDTO> getProductsList(int pageIndex, int pageSize);

    public ReportingDTO getProductsSummary(LocalDateTime date);
}
