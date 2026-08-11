package com.akshat.shopsphere.product.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.product.dto.ProductCreateRequest;
import com.akshat.shopsphere.product.dto.ProductResponse;
import com.akshat.shopsphere.product.dto.ProductUpdateRequest;
import com.akshat.shopsphere.product.service.ProductService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request
                                                                        ,Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        ProductResponse response = productService.createProduct(request, userDetails.getId());

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(true,
                "Product created successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);

    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long productId){

        ProductResponse response = productService.getProductById(productId);

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(true
        ,"Product fetched successfully"
        ,response
        ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductUpdateRequest request,
                                                                           @PathVariable Long productId,
                                                                           Authentication authentication){

        UserDetailsImplementation userDetails =
                getAuthenticatedUser(authentication);

        ProductResponse response = productService.
                updateProduct(productId
                ,userDetails.getId()
                ,request
                ,userDetails.getRole());

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(true
                ,"Product updated successfully"
                ,response
                ,LocalDateTime.now());

        return  ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId,Authentication authentication){

    UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);



        productService.deleteProduct(productId, userDetails.getId(), userDetails.getRole());

        ApiResponse<Void> apiResponse = new ApiResponse<>(true
                ,"Product soft deleted successfully"
                ,null
                ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){

         return  (UserDetailsImplementation) authentication.getPrincipal();

    }
}
