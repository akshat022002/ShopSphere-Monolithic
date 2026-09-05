package com.akshat.shopsphere.review.service;

import com.akshat.shopsphere.review.dto.CreateReviewRequest;
import com.akshat.shopsphere.review.dto.ReviewResponse;
import com.akshat.shopsphere.review.dto.UpdateReviewRequest;
import com.akshat.shopsphere.user.enums.Role;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request);

    ReviewResponse getReviewById(Long reviewId);

    List<ReviewResponse> getProductReviews(Long productId);

    ReviewResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long userId, Role role, Long reviewId);
}
