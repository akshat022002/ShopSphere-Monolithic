package com.akshat.shopsphere.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(

        @Size(max = 150,message ="Product name must be under 150 characters" )
        String name,

        @Size(max=2000,message = "Product description must be under 2000 characters")
        String description,

        @DecimalMin(value = "0.01",message = "Price must be greater than 0")
        BigDecimal price

) {
}
