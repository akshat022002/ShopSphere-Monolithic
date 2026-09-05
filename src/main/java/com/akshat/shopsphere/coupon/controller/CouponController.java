package com.akshat.shopsphere.coupon.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.coupon.dto.CouponResponse;
import com.akshat.shopsphere.coupon.dto.CreateCouponRequest;
import com.akshat.shopsphere.coupon.dto.UpdateCouponRequest;
import com.akshat.shopsphere.coupon.service.CouponService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CreateCouponRequest request,
                                                                    Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        CouponResponse response = couponService.createCoupon(userDetails.getId(), userDetails.getRole(),request);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                                         "Coupon created successfully",
                                                   response,
                                                   LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{couponId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(@PathVariable Long couponId){

        CouponResponse response = couponService.getCouponById(couponId);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                "Coupon fetched successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{couponId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(@PathVariable Long couponId,
                                                                    @RequestBody @Valid UpdateCouponRequest request,
                                                                    Authentication authentication){


        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        CouponResponse response = couponService.updateCoupon(userDetails.getId(), userDetails.getRole(),couponId,request);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                "Coupon updated successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<CouponResponse>> deleteCoupon(@PathVariable Long couponId,
                                                                    Authentication authentication){


        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        couponService.deleteCoupon(userDetails.getId(), userDetails.getRole(),couponId);

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>(true,
                "Coupon soft deleted/deactivated successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }


    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){

        return  (UserDetailsImplementation) authentication.getPrincipal();

    }
}
