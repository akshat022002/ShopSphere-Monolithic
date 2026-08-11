package com.akshat.shopsphere.product.mapper;

import com.akshat.shopsphere.product.dto.ProductCreateRequest;
import com.akshat.shopsphere.product.dto.ProductResponse;
import com.akshat.shopsphere.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "seller",ignore = true)
    @Mapping(target = "active",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    Product toEntity(ProductCreateRequest request);


    @Mapping(target = "sellerId",source = "seller.id")
    ProductResponse toResponse(Product product);
}
