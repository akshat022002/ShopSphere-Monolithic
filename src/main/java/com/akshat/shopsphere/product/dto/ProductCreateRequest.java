package com.akshat.shopsphere.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 150,message ="Product name must be under 150 characters" )
        String name,

        @NotBlank(message = "Product description is required")
        @Size(max=2000,message = "Product description must be under 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.1",message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Sku is required")
        @Size(max = 50, message = "Sku must be under 50 characters")
        String sku

) {

}
