package com.akshat.shopsphere.category.mapper;

import com.akshat.shopsphere.category.dto.CategoryCreateRequest;
import com.akshat.shopsphere.category.dto.CategoryResponse;
import com.akshat.shopsphere.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = SubcategoryMapper.class)
public interface CategoryMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "active",ignore = true)
    @Mapping(target = "subcategories",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    public Category toEntity(CategoryCreateRequest request);


    public CategoryResponse toResponse(Category category);
}
