package com.akshat.shopsphere.product.repository;

import com.akshat.shopsphere.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {

    List<ProductImage> findAllByProductId(Long productId);

    Optional<ProductImage> findByIdAndProductId(Long imageId,Long productId);

    boolean existsByProductIdAndIsPrimary(Long productId,boolean isPrimary);
}
