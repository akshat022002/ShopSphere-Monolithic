package com.akshat.shopsphere.cart.mapper;

import com.akshat.shopsphere.cart.dto.CartItemResponse;
import com.akshat.shopsphere.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

@Mapping(target = "productId",source = "product.id")
@Mapping(target = "productName",source = "product.name")
@Mapping(target = "price",source = "product.price")
@Mapping(target = "subtotal",ignore = true)
public CartItemResponse toResponse(CartItem cartItem);
}
