package com.akshat.shopsphere.order.mapper;

import com.akshat.shopsphere.order.dto.OrderResponse;
import com.akshat.shopsphere.order.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

public OrderResponse toResponse(Order order);

}
