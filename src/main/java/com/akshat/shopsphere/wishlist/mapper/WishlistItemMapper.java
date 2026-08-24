package com.akshat.shopsphere.wishlist.mapper;

import com.akshat.shopsphere.wishlist.dto.WishlistItemResponse;
import com.akshat.shopsphere.wishlist.entity.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistItemMapper {

    @Mapping(target = "productId",source ="product.id" )
    @Mapping(target = "productName",source = "product.name")
    @Mapping(target = "price",source = "product.price")
    public WishlistItemResponse toResponse(WishlistItem wishlistItem);
}
