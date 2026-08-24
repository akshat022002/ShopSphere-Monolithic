package com.akshat.shopsphere.wishlist.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import com.akshat.shopsphere.wishlist.dto.WishlistResponse;
import com.akshat.shopsphere.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(Authentication authentication){

    UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);
    Long userId = userDetails.getId();

    WishlistResponse response = wishlistService.getWishlist(userId);

    ApiResponse<WishlistResponse> apiResponse = new ApiResponse<>(true,
            "Wishlist fetched successfully",
            response,
            LocalDateTime.now());

    return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(@PathVariable Long productId,
                                                                       Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);
        Long userId = userDetails.getId();

        WishlistResponse response = wishlistService.addToWishlist(userId,productId);

        ApiResponse<WishlistResponse> apiResponse = new ApiResponse<>(true,
                "Product added to wishlist successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(@PathVariable Long productId,
                                                                Authentication authentication){
        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);
        Long userId = userDetails.getId();

        wishlistService.removeFromWishlist(userId,productId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Product removed from wishlist successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearWishlist(Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);
        Long userId = userDetails.getId();

        wishlistService.clearWishlist(userId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Wishlist cleared  successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){
        return (UserDetailsImplementation) authentication.getPrincipal();
    }
}
