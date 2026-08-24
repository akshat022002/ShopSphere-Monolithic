package com.akshat.shopsphere.category.controller;

import com.akshat.shopsphere.category.dto.*;
import com.akshat.shopsphere.category.service.CategoryService;
import com.akshat.shopsphere.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    //CATEGORY CONTROLLER

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest request){

        CategoryResponse response = categoryService.createCategory(request);

        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(true
                ,"Category created successfully"
                ,response, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);

    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId){

        CategoryResponse response = categoryService.getCategoryById(categoryId);

        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(true
                ,"Category fetched successfully"
                ,response, LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){

        List<CategoryResponse> responses = categoryService.getAllCategories();

        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>(true
                ,"Categories fetched successfully"
                ,responses, LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long categoryId
            ,@Valid @RequestBody CategoryUpdateRequest request){

        CategoryResponse response = categoryService.updateCategory(categoryId,request);

        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(true
                ,"Categories updated successfully"
                ,response, LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId){

        categoryService.deleteCategory(categoryId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true
                ,"Categories deleted successfully"
                ,null, LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    //SUBCATEGORY CONTROLLER

    @PostMapping("/{categoryId}/subcategories")
    public ResponseEntity<ApiResponse<SubcategoryResponse>> createSubcategory(@PathVariable Long categoryId
            , @Valid @RequestBody SubcategoryCreateRequest request){

        SubcategoryResponse response = categoryService.createSubcategories(categoryId,request);

        ApiResponse<SubcategoryResponse> apiResponse = new ApiResponse<>(true
                ,"Subcategory created successfully"
                ,response,LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/subcategories/{subcategoryId}")
    public ResponseEntity<ApiResponse<SubcategoryResponse>> getSubcategoryById(@PathVariable Long subcategoryId){

        SubcategoryResponse response = categoryService.getSubcategoryById(subcategoryId);

        ApiResponse<SubcategoryResponse> apiResponse = new ApiResponse<>(true
                ,"Subcategory fetched successfully"
                ,response,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<ApiResponse<List<SubcategoryResponse>>> getSubcategoriesByCategory(@PathVariable Long categoryId){

        List<SubcategoryResponse> responses = categoryService.getSubcategoriesByCategory(categoryId);

        ApiResponse<List<SubcategoryResponse>> apiResponse = new ApiResponse<>(true
                ,"Subcategories fetched successfully"
                ,responses,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }
    @PatchMapping("/subcategories/{subcategoryId}")
    public ResponseEntity<ApiResponse<SubcategoryResponse>> updateSubcategory(@PathVariable Long subcategoryId
            ,@Valid @RequestBody SubcategoryUpdateRequest request){

        SubcategoryResponse response = categoryService.updateSubcategory(subcategoryId,request);

        ApiResponse<SubcategoryResponse> apiResponse = new ApiResponse<>(true
                ,"Subcategory updated successfully"
                ,response,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @DeleteMapping("/subcategories/{subcategoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteSubcategory(@PathVariable Long subcategoryId){

        categoryService.deleteSubcategory(subcategoryId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true
                ,"Subcategory deleted successfully"
                ,null,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

}
