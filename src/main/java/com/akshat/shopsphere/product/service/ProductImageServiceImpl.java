package com.akshat.shopsphere.product.service;

import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.product.dto.ProductImageResponse;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.entity.ProductImage;
import com.akshat.shopsphere.product.mapper.ProductImageMapper;
import com.akshat.shopsphere.product.repository.ProductImageRepository;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.user.enums.Role;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService{

    private final Cloudinary cloudinary;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    public ProductImageResponse uploadImage(Long productId, Long userId, MultipartFile file, boolean isPrimary) {

        Product product = productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with id : "+productId));

        if(!product.getSeller().getId().equals(userId)){
            throw new AccessDeniedException("You are not authorized to upload images for this product");
        }

        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("Image file is required");
        }

        try{
            var uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap(
                    "folder","shopsphere/products/"+productId
            ));

            String imageUrl =(String) uploadResult.get("secure_url");

            String storageKey=(String) uploadResult.get("public_id");

            if(isPrimary){
                productImageRepository.findAllByProductId(productId)
                        .forEach(image->
                                image.setPrimary(false)
                                );
            }

            ProductImage productImage = ProductImage.builder()
                    .imageUrl(imageUrl)
                    .storageKey(storageKey)
                    .isPrimary(isPrimary)
                    .product(product)
                    .build();

            ProductImage savedImage = productImageRepository.save(productImage);

            return productImageMapper.toResponse(savedImage);
        }

        catch (IOException ex) {
            log.error("Failed to upload image for product : {}",productId,ex);

            throw new RuntimeException("Failed to upload image");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(Long productId) {

       productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with id : "+productId));

        return productImageRepository
                .findAllByProductId(productId)
                .stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteImage(Long productId ,Long imageId, Long userId, Role role) {

        ProductImage image = productImageRepository.findByIdAndProductId(imageId,productId)
                .orElseThrow(()-> new ResourceNotFoundException("Image not found for this product"));


        Product product = image.getProduct();

        boolean isAdmin = Role.ROLE_ADMIN.equals(role);

        boolean isOwner = product.getSeller().getId().equals(userId);

        if(!product.isActive() && !isAdmin){
            throw new ResourceNotFoundException("Product not found with id : "+product.getId());
        }

        if(!isAdmin && !isOwner){
            throw new AccessDeniedException("You are not authorized to delete this image");
        }

        try{
            cloudinary.uploader().destroy(image.getStorageKey(), ObjectUtils.emptyMap());

            productImageRepository.delete(image);

            log.info("Product image deleted successfully: {}", imageId);
        }

        catch (IOException ex) {
            log.error("Failed to delete image : {}",imageId,ex);
            throw new RuntimeException("Failed to delete product image");
        }
    }

    @Override
    public void setPrimaryImage(Long productId,Long imageId,Long userId, Role role) {

        ProductImage image = productImageRepository.findByIdAndProductId(imageId,productId)
                .orElseThrow(()->new ResourceNotFoundException("Image not found for this product "));

        Product product = image.getProduct();

        boolean isAdmin = Role.ROLE_ADMIN.equals(role);
        boolean isOwner = product.getSeller().getId().equals(userId);

        if (!product.isActive()) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + product.getId()
            );
        }

        if(!isAdmin && !isOwner){
            throw new AccessDeniedException("You are not authorized to modify this image");
        }

        productImageRepository.findAllByProductId(productId)
                .forEach(images->
                        images.setPrimary(images.getId().equals(imageId))
                        );

        log.info("Primary image changed to: {}", imageId);

    }
}
