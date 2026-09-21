package com.akshat.shopsphere.product.service;

import com.akshat.shopsphere.product.dto.*;
import com.akshat.shopsphere.user.enums.Role;


public interface ProductService {


    ProductResponse createProduct(ProductCreateRequest request , Long sellerId);

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, Long sellerId, ProductUpdateRequest request, Role role) ;

    void deleteProduct(Long productId,Long sellerId,Role role) ;

    ProductSearchResponse searchProducts(ProductSearchRequest request);

}
