package com.akshat.shopsphere.product.service;

import com.akshat.shopsphere.audit.annotation.Audit;
import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;
import com.akshat.shopsphere.product.dto.*;
import com.akshat.shopsphere.user.enums.Role;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;

public interface ProductService {



    ProductResponse createProduct(ProductCreateRequest request , Long sellerId);

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, Long sellerId, ProductUpdateRequest request, Role role) ;

    void deleteProduct(Long productId,Long sellerId,Role role) ;

    ProductSearchResponse searchProducts(ProductSearchRequest request);

}
