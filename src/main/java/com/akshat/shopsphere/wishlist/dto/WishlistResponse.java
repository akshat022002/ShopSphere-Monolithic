package com.akshat.shopsphere.wishlist.dto;

import java.math.BigDecimal;
import java.util.List;

public record WishlistResponse(
        Long wishlistId,
        List<WishlistItemResponse> items

) {
}
