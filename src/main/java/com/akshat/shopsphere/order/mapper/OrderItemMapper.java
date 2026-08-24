package com.akshat.shopsphere.order.mapper;

import com.akshat.shopsphere.order.dto.OrderItemResponse;
import com.akshat.shopsphere.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId",source = "product.id")
    @Mapping(target = "productName",source = "product.name")
    public OrderItemResponse toResponse(OrderItem orderItem);
}
