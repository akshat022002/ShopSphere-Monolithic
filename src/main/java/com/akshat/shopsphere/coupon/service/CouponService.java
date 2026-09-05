package com.akshat.shopsphere.coupon.service;

import com.akshat.shopsphere.cart.entity.CartItem;
import com.akshat.shopsphere.coupon.dto.CouponResponse;
import com.akshat.shopsphere.coupon.dto.CreateCouponRequest;
import com.akshat.shopsphere.coupon.dto.UpdateCouponRequest;
import com.akshat.shopsphere.user.enums.Role;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    CouponResponse createCoupon(Long userId , Role role , CreateCouponRequest request);

    CouponResponse getCouponById(Long couponId);

    CouponResponse updateCoupon(Long userId, Role role , Long couponId , UpdateCouponRequest request);

    void deleteCoupon(Long userId, Role role , Long couponId);

    BigDecimal calculateDiscount(String code, List<CartItem> cartItems);
}
