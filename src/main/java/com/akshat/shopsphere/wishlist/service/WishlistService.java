package com.akshat.shopsphere.wishlist.service;

import com.akshat.shopsphere.wishlist.dto.WishlistResponse;

public interface WishlistService {

    WishlistResponse getWishlist(Long userId);

    WishlistResponse addToWishlist(Long userId, Long productId);

    void removeFromWishlist(Long userId,Long productId);

    void clearWishlist(Long userId);
}
