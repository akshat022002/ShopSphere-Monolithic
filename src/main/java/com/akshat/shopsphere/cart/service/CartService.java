package com.akshat.shopsphere.cart.service;

import com.akshat.shopsphere.cart.dto.AddToCartRequest;
import com.akshat.shopsphere.cart.dto.CartResponse;
import com.akshat.shopsphere.cart.dto.UpdateCartItemRequest;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addToCart(Long userId, Long productId, AddToCartRequest request);

    CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request);

    void removeCartItem(Long userId, Long productId);

    void clearCart(Long userId);
}
