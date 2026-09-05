package com.akshat.shopsphere.review.service;

import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.order.enums.OrderStatus;
import com.akshat.shopsphere.order.repository.OrderItemRepository;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.review.dto.CreateReviewRequest;
import com.akshat.shopsphere.review.dto.ReviewResponse;
import com.akshat.shopsphere.review.dto.UpdateReviewRequest;
import com.akshat.shopsphere.review.entity.Review;
import com.akshat.shopsphere.review.mapper.ReviewMapper;
import com.akshat.shopsphere.review.repository.ReviewRepository;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.enums.Role;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService{

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;


    @Override
    public ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request) {

        Product product = productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(()->
                        new ResourceNotFoundException("Product not found with id : "+productId));

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new
                        ResourceNotFoundException("User not found with id : "+userId));

        if(reviewRepository.existsByUserIdAndProductIdAndActiveTrue(userId,productId)){
            throw new DuplicateResourceException("You have already reviewed this product");
        }

        boolean purchased = orderItemRepository
                .existsByUserAndProductAndOrderStatus(userId,productId, OrderStatus.DELIVERED);

        if(!purchased){
            throw new AccessDeniedException("You can only review products you have purchased");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(savedReview);
    }

    @Transactional(readOnly = true)
    @Override
    public ReviewResponse getReviewById(Long reviewId) {

        return reviewRepository.findById(reviewId)
                .filter(Review::isActive)
                .map(reviewMapper::toResponse)
                .orElseThrow(()-> new  ResourceNotFoundException("Review not found with id : "+reviewId));

    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> getProductReviews(Long productId) {

        productRepository.findById(productId)
                .orElseThrow(()->new
                        ResourceNotFoundException("Product not found with id : "+productId));

        return reviewRepository.findAllByProductIdAndActiveTrue(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public ReviewResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request) {

        Review review = reviewRepository.findById(reviewId)
                .filter(Review::isActive)
                .orElseThrow(()-> new ResourceNotFoundException("Review not found with id : "+reviewId));

        if(!review.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You are not authorized to update this review");
        }

        review.setRating(request.rating());
        review.setComment(request.comment());

        return reviewMapper.toResponse(review);
    }

    @Override
    public void deleteReview(Long userId, Role role, Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .filter(Review::isActive)
                .orElseThrow(()-> new
                        ResourceNotFoundException("Review not found with id : "+reviewId));

        if(Role.ROLE_ADMIN.equals(role)){
            review.setActive(false);
            return;
        }

        if(Role.ROLE_SELLER.equals(role) && review.getProduct().getSeller().getId().equals(userId)){
            review.setActive(false);
            return;
        }

        if(Role.ROLE_CUSTOMER.equals(role) && review.getUser().getId().equals(userId)) {
            review.setActive(false);
            return;
        }

        throw new AccessDeniedException("You are not authorized to delete reviews");
    }
}
