package com.commercetools.aggregator.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(nullable = false)
    private String uuid;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String provider;

    @Column
    private boolean available;

    @Column
    private String measurementUnit;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

}
