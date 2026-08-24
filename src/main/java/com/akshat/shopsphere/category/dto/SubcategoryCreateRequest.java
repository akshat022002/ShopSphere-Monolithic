package com.akshat.shopsphere.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubcategoryCreateRequest(

        @NotBlank(message = "Subcategory name is required")
        @Size(max =100,message = "Subcategory name must not exceed 100 characters")
        String name,

        @Size(max = 500,message = "Subcategory description must not exceed 500 characters")
        String description

) {
}
