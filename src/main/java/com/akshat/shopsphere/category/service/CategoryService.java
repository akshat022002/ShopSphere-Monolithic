package com.akshat.shopsphere.category.service;

import com.akshat.shopsphere.category.dto.*;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse getCategoryById(Long categoryId);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request);

    void deleteCategory(Long categoryId);

    SubcategoryResponse createSubcategories(Long categoryId, SubcategoryCreateRequest request);

    SubcategoryResponse getSubcategoryById(Long subcategoryId);

    List<SubcategoryResponse> getSubcategoriesByCategory(Long categoryId);

    SubcategoryResponse updateSubcategory(Long subcategoryId, SubcategoryUpdateRequest request);

    void deleteSubcategory(Long subcategoryId);
}
