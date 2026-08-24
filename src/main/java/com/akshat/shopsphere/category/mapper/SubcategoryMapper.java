package com.akshat.shopsphere.category.mapper;

import com.akshat.shopsphere.category.dto.SubcategoryCreateRequest;
import com.akshat.shopsphere.category.dto.SubcategoryResponse;
import com.akshat.shopsphere.category.entity.Subcategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "category",ignore = true )
    @Mapping(target = "active",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    public Subcategory toEntity(SubcategoryCreateRequest request);

    @Mapping(target = "categoryId",source = "category.id")
    public SubcategoryResponse toResponse(Subcategory subcategory);
}
