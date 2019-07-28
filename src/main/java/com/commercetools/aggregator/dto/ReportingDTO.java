package com.commercetools.aggregator.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class ReportingDTO implements Serializable {

    private final long numberOfCreatedProducts;
    private final long numberOfUpdatedProducts;

}
