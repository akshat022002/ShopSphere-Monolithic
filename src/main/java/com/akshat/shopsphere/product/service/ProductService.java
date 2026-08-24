package com.akshat.shopsphere.product.service;

import com.akshat.shopsphere.product.dto.ProductCreateRequest;
import com.akshat.shopsphere.product.dto.ProductResponse;
import com.akshat.shopsphere.product.dto.ProductSearchRequest;
import com.akshat.shopsphere.product.dto.ProductUpdateRequest;
import com.akshat.shopsphere.user.enums.Role;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request , Long sellerId);

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, Long sellerId, ProductUpdateRequest request, Role role) ;

    void deleteProduct(Long productId,Long sellerId,Role role) ;

    Page<ProductResponse> searchProducts(ProductSearchRequest request);

}
