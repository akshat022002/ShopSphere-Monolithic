package com.akshat.shopsphere.product.dto;

import java.util.List;

public record ProductSearchResponse(
        List<ProductResponse> content,
        int page,
        int size
) {
}
