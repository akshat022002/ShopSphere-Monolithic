package com.akshat.shopsphere.product.service;

import com.akshat.shopsphere.product.dto.ProductImageResponse;
import com.akshat.shopsphere.user.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse uploadImage(Long productId, Long userId, MultipartFile file,boolean isPrimary);

    List<ProductImageResponse> getProductImages(Long productId);

    void deleteImage(Long productId,Long imageId,Long userId, Role role);

    void setPrimaryImage(Long productId,Long imageId,Long userId, Role role);
}
