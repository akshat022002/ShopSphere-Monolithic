package com.akshat.shopsphere.product.mapper;

import com.akshat.shopsphere.product.dto.ProductImageResponse;
import com.akshat.shopsphere.product.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(target = "isPrimary",source = "primary")
    public ProductImageResponse toResponse(ProductImage productImage);
}
