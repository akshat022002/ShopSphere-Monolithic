package com.akshat.shopsphere.review.mapper;

import com.akshat.shopsphere.review.dto.ReviewResponse;
import com.akshat.shopsphere.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    @Mapping(target = "productId",source = "product.id")
    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "username",source = "user.name")
    ReviewResponse toResponse(Review review);
}
