package com.akshat.shopsphere.coupon.mapper;

import com.akshat.shopsphere.coupon.dto.CouponResponse;
import com.akshat.shopsphere.coupon.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(target = "productId",source = "product.id")
    public CouponResponse toResponse(Coupon coupon);

}
