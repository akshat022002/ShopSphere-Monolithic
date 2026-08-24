package com.akshat.shopsphere.cart.controller;

import com.akshat.shopsphere.cart.dto.AddToCartRequest;
import com.akshat.shopsphere.cart.dto.CartResponse;
import com.akshat.shopsphere.cart.dto.UpdateCartItemRequest;
import com.akshat.shopsphere.cart.service.CartService;
import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication){

    UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

    Long userId = userDetails.getId();

    CartResponse response = cartService.getCart(userId);

    ApiResponse<CartResponse> apiResponse = new ApiResponse<>(true,
            "Cart fetched successfully",
            response,
            LocalDateTime.now());

    return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@PathVariable Long productId,
                                                               @Valid @RequestBody AddToCartRequest request,Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        Long userId = userDetails.getId();

        CartResponse response = cartService.addToCart(userId,productId,request);

        ApiResponse<CartResponse> apiResponse = new ApiResponse<>(true,
                "Product added to cart successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(@PathVariable Long productId,
                                                                    @Valid @RequestBody UpdateCartItemRequest request,
                                                                    Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        Long userId = userDetails.getId();

        CartResponse response = cartService.updateCartItem(userId,productId,request);

        ApiResponse<CartResponse> apiResponse = new ApiResponse<>(true,
                "Cart item updated successfully",
                response,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(@PathVariable Long productId,
                                                            Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        Long userId = userDetails.getId();

        cartService.removeCartItem(userId,productId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Cart item removed successfully",
                null,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        Long userId = userDetails.getId();

        cartService.clearCart(userId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Cart cleared successfully",
                null,LocalDateTime.now());

        return  ResponseEntity.ok(apiResponse);
    }


    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){
        return (UserDetailsImplementation) authentication.getPrincipal();
    }

}
