package com.akshat.shopsphere.review.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.review.dto.CreateReviewRequest;
import com.akshat.shopsphere.review.dto.ReviewResponse;
import com.akshat.shopsphere.review.dto.UpdateReviewRequest;
import com.akshat.shopsphere.review.service.ReviewService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

        private final ReviewService reviewService;


        @PostMapping("/products/{productId}/reviews")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ResponseEntity<ApiResponse<ReviewResponse>> createReview(@PathVariable Long productId,
                                                                        @RequestBody @Valid CreateReviewRequest request,
                                                                        Authentication authentication){
            UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

            ReviewResponse response = reviewService.createReview(userDetails.getId(), productId,request);

            ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(true,
                   "Review created successfully",
                    response,
                    LocalDateTime.now());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(apiResponse);

        }

        @GetMapping("/reviews/{reviewId}")
        public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable Long reviewId){

            ReviewResponse response = reviewService.getReviewById(reviewId);

            ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(true,
                    "Review fetched successfully",
                    response,
                    LocalDateTime.now());

            return ResponseEntity
                    .ok(apiResponse);

        }

        @GetMapping("/products/{productId}/reviews")
        public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(@PathVariable Long productId){

            List<ReviewResponse> responses = reviewService.getProductReviews(productId);

            ApiResponse<List<ReviewResponse>> apiResponse = new ApiResponse<>(true,
                    "Product reviews fetched successfully",
                    responses,
                    LocalDateTime.now());

            return ResponseEntity
                    .ok(apiResponse);

        }


    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(@PathVariable Long reviewId,
                                                                    @RequestBody @Valid UpdateReviewRequest request,
                                                                    Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        ReviewResponse response = reviewService.updateReview(userDetails.getId(),reviewId,request);

        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(true,
                "Review updated successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity
                .ok(apiResponse);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId,
                                                          Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        reviewService.deleteReview(userDetails.getId(),userDetails.getRole(),reviewId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Review soft deleted successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity
                .ok(apiResponse);
    }

        private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){
            return (UserDetailsImplementation) authentication.getPrincipal();
        }

}
