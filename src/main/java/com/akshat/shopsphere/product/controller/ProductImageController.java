package com.akshat.shopsphere.product.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.product.dto.ProductImageResponse;
import com.akshat.shopsphere.product.service.ProductImageService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import com.akshat.shopsphere.user.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {

    private final ProductImageService productImageService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductImageResponse>> uploadImage(@PathVariable Long productId
        ,@RequestParam("file") MultipartFile file
        ,@RequestParam(name = "isPrimary",defaultValue = "false") boolean isPrimary
        ,Authentication authentication){

    UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

    ProductImageResponse response = productImageService.uploadImage(productId
            ,userDetails.getId()
            ,file,isPrimary);

    ApiResponse<ProductImageResponse> apiResponse = new ApiResponse<>(true
            ,"Product image uploaded successfully",
            response, LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(apiResponse);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getImages(@PathVariable Long productId){

        List<ProductImageResponse> responses = productImageService.getProductImages(productId);

        ApiResponse<List<ProductImageResponse>> apiResponse = new ApiResponse<>(true
                ,"Product images fetched successfully"
                ,responses,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId,@PathVariable Long productId,
                                                         Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        Long userId = userDetails.getId();
        Role role = userDetails.getRole();

        productImageService.deleteImage(productId,imageId,userId,role);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true
                ,"Product image deleted successfully"
                ,null,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{imageId}/primary")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> setPrimaryImage(@PathVariable Long imageId,@PathVariable Long productId
            ,Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        Long userId = userDetails.getId();
        Role role = userDetails.getRole();

        productImageService.setPrimaryImage(productId,imageId,userId,role);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true
                ,"Product primary image set successfully"
                ,null,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){

        return (UserDetailsImplementation) authentication.getPrincipal();
    }
}
