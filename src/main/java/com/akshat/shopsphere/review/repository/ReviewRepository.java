package com.akshat.shopsphere.review.repository;

import com.akshat.shopsphere.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    Optional<Review> findByUserIdAndProductId(Long userId,Long productId);

    boolean existsByUserIdAndProductIdAndActiveTrue(Long userId,Long productId);

    List<Review> findAllByProductIdAndActiveTrue(Long productId);
}
