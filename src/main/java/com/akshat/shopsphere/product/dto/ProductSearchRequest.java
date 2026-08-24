package com.akshat.shopsphere.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record ProductSearchRequest(
        String name,

        Long categoryId,

        Long subcategoryId,

        @DecimalMin(value = "0.01",message = "Minimum price must be greater than 0")
        BigDecimal minPrice,

        @DecimalMin(value = "0.01",message = "Maximum price must be greater than 0")
        BigDecimal maxPrice,

        @Min(value = 0,message = "Page cannot be negative")
        Integer page,

        @Min(value = 1,message = "Size must be at least 1")
        Integer size,

        String sortBy,

        String sortDir

) {
}
