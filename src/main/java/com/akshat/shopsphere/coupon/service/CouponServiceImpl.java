package com.akshat.shopsphere.coupon.service;

import com.akshat.shopsphere.cart.entity.CartItem;
import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.coupon.dto.CouponResponse;
import com.akshat.shopsphere.coupon.dto.CreateCouponRequest;
import com.akshat.shopsphere.coupon.dto.UpdateCouponRequest;
import com.akshat.shopsphere.coupon.entity.Coupon;
import com.akshat.shopsphere.coupon.enums.DiscountType;
import com.akshat.shopsphere.coupon.mapper.CouponMapper;
import com.akshat.shopsphere.coupon.repository.CouponRepository;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService{

    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Override
    public CouponResponse createCoupon(Long userId, Role role, CreateCouponRequest request) {

        if(couponRepository.existsByCodeIgnoreCase(request.code())){
            throw new DuplicateResourceException("Coupon already exists with code : "+request.code());
        }

        Product product = null;

        if(request.productId()!=null){

           product=productRepository.findById(request.productId())
                   .filter(Product::isActive)
                   .orElseThrow(()-> new
                           ResourceNotFoundException("Product not found with id : "+request.productId()));

           if(Role.ROLE_SELLER.equals(role) && !product.getSeller().getId().equals(userId)){
               throw new AccessDeniedException("You are not authorized to create a coupon for this product");
           }

        }

        if(Role.ROLE_SELLER.equals(role) && request.productId()==null){
            throw new IllegalArgumentException("Seller must specify a product for coupon");
        }

        Coupon coupon = Coupon.builder()
                .discountType(request.discountType())
                .discountValue(request.discountValue())
                .expiresAt(request.expiresAt())
                .usageLimit(request.usageLimit())
                .product(product)
                .code(request.code().trim().toUpperCase())
                .maximumDiscount(request.maximumDiscount())
                .minimumOrderAmount(request.minimumOrderAmount())
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);

        return couponMapper.toResponse(savedCoupon);
    }

    @Transactional(readOnly = true)
    @Override
    public CouponResponse getCouponById(Long couponId) {

        return couponRepository.findById(couponId)
                .map(couponMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Coupon not found with id : "+couponId));

    }

    @Override
    public CouponResponse updateCoupon(Long userId, Role role, Long couponId, UpdateCouponRequest request) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(()-> new ResourceNotFoundException("Coupon not found with id : "+couponId));

        validateCouponOwnership(coupon,userId,role);

        if(request.discountValue()!=null){
            coupon.setDiscountValue(request.discountValue());
        }

        if (request.minimumOrderAmount() != null) {
            coupon.setMinimumOrderAmount(request.minimumOrderAmount());
        }

        if (request.maximumDiscount() != null) {
            coupon.setMaximumDiscount(request.maximumDiscount());
        }

        if (request.expiresAt() != null) {
            coupon.setExpiresAt(request.expiresAt());
        }

        if (request.usageLimit() != null) {
            coupon.setUsageLimit(request.usageLimit());
        }

        if (request.active() != null) {
            coupon.setActive(request.active());
        }

        return couponMapper.toResponse(coupon);

    }

    @Override
    public void deleteCoupon(Long userId, Role role, Long couponId) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(()-> new ResourceNotFoundException("Coupon not found with id : "+couponId));

        validateCouponOwnership(coupon,userId,role);

        coupon.setActive(false);

    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal calculateDiscount(String code, List<CartItem> cartItems) {

        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(()-> new ResourceNotFoundException("Coupon not found : "+code));

        BigDecimal eligibleAmount = calculateEligibleAmount(coupon,cartItems);

        validateCoupon(coupon,eligibleAmount);

        BigDecimal discount;

        if(coupon.getDiscountType()==DiscountType.PERCENTAGE){

            discount = eligibleAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);

            if(coupon.getMaximumDiscount()!=null){
                discount=discount.min(coupon.getMaximumDiscount());
            }

        } else{
            discount = coupon.getDiscountValue();
        }

        return discount.min(eligibleAmount);

    }

    private void validateCouponOwnership(Coupon coupon, Long userId,Role role){

        Product product = coupon.getProduct();

        if(Role.ROLE_ADMIN.equals(role)){
            return;
        }

        if(product==null || !product.getSeller().getId().equals(userId)){
            throw new AccessDeniedException("You are not authorized to manage this coupon");
        }
    }

    private void validateCoupon(Coupon coupon , BigDecimal eligibleAmount){

        if(!coupon.isActive()){
            throw new IllegalStateException("Coupon is inactive");
        }

        if(LocalDateTime.now().isAfter(coupon.getExpiresAt())){
            throw new IllegalStateException("Coupon has expired");
        }

        if(coupon.getUsageCount()>=coupon.getUsageLimit()){
            throw new IllegalStateException("Coupon usage limit reached");
        }

        if(coupon.getMinimumOrderAmount()!=null && eligibleAmount.compareTo(coupon.getMinimumOrderAmount())<0){
            throw new IllegalArgumentException("Minimum order amount is : "+coupon.getMinimumOrderAmount());
        }

        if(coupon.getDiscountType()== DiscountType.PERCENTAGE &&
                coupon.getDiscountValue().compareTo(BigDecimal.valueOf(75))>0){

            throw new IllegalArgumentException("Percentage discount cannot exceed 75%");
        }

        if(eligibleAmount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Coupon is not applicable to this cart");
        }

    }

    private BigDecimal calculateEligibleAmount(Coupon coupon,List<CartItem> cartItems){

        if(coupon.getProduct()==null){

            return cartItems.stream()
                    .map(
                            item -> item.getProduct().getPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    )
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
        }

        return cartItems.stream()
                .filter(item->
                        item.getProduct().getId().equals(coupon.getProduct().getId())
                )
                .map( item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

    }

}
