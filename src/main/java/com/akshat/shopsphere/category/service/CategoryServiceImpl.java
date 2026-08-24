package com.akshat.shopsphere.category.service;

import com.akshat.shopsphere.category.dto.*;
import com.akshat.shopsphere.category.entity.Category;
import com.akshat.shopsphere.category.entity.Subcategory;
import com.akshat.shopsphere.category.mapper.CategoryMapper;
import com.akshat.shopsphere.category.mapper.SubcategoryMapper;
import com.akshat.shopsphere.category.repository.CategoryRepository;
import com.akshat.shopsphere.category.repository.SubcategoryRepository;
import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SubcategoryMapper subcategoryMapper;
    private final CategoryMapper categoryMapper;


    //CATEGORY SERVICE IMPL
    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        log.info("Creating category with name :{}",request.name());

       Category existingCategory = categoryRepository
               .findByNameIgnoreCase(request.name())
               .orElse(null);

       if(existingCategory!=null && existingCategory.isActive()){
           throw new DuplicateResourceException("Category already exists : "+request.name());
       }

        Category category = categoryMapper.toEntity(request);

        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with id :{} ",savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);

    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse getCategoryById(Long categoryId) {

      Category existingCategory = categoryRepository
              .findById(categoryId)
              .orElseThrow(()-> new
                      ResourceNotFoundException("Category not exists with id :"+categoryId));

      if(!existingCategory.isActive()){
        throw new ResourceNotFoundException("Category not exists with id : "+categoryId);
      }

      return categoryMapper.toResponse(existingCategory);

    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .filter(Category::isActive)
                .map(
                        category -> {
                            category.setSubcategories((
                                    category.getSubcategories().stream()
                                            .filter(Subcategory::isActive)
                                            .toList()
                                    ));
                            return categoryMapper.toResponse(category);
                        }
                )
                .toList();

    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {

        Category existingCategory = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category not found with ID : "+categoryId));

        if(!existingCategory.isActive()){

            throw new ResourceNotFoundException("Category not found with id : "+categoryId);
        }

        if(request.name()!=null && !existingCategory.getName().equalsIgnoreCase(request.name())){

           categoryRepository.findByNameIgnoreCase(request.name())
                   .ifPresent(existing -> {
                       if(existing.isActive()){

                           throw new DuplicateResourceException("Category already exists : "+ request.name());
                       }
                   });
            existingCategory.setName(request.name());

        }

        if(request.description()!=null){
            existingCategory.setDescription(request.description());
        }

        Category updatedCategory = categoryRepository.save(existingCategory);

        return categoryMapper.toResponse(updatedCategory);

    }

    @Override
    public void deleteCategory(Long categoryId) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found with id : "+categoryId));

        if(!category.isActive()){
            throw new ResourceNotFoundException("Category not found with id : "+categoryId);
        }

        category.setActive(false);

        log.info("Category soft deleted successfully : {}",categoryId);

    }


    //SUBCATEGORY SERVICE IMPL
    @Override
    public SubcategoryResponse createSubcategories(Long categoryId,SubcategoryCreateRequest request) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category not found with id : "+categoryId));

        if(!category.isActive()){
            throw new IllegalStateException("Cannot create subcategory under an inactive category");
        }

        Subcategory existingSubcategory = subcategoryRepository
                .findByNameIgnoreCaseAndCategoryId(request.name(),categoryId).orElse(null);

        if(existingSubcategory!=null && existingSubcategory.isActive()){
            throw new DuplicateResourceException("Subcategory already exists : "+request.name());
        }

        Subcategory subcategory = subcategoryMapper.toEntity(request);

        subcategory.setCategory(category);
        subcategory.setActive(true);

        Subcategory savedSubcategory = subcategoryRepository.save(subcategory);

        log.info("Subcategory created successfully with id: {}",
                savedSubcategory.getId());

        return subcategoryMapper.toResponse(savedSubcategory);

    }

    @Override
    @Transactional(readOnly = true)
    public SubcategoryResponse getSubcategoryById(Long subcategoryId) {

        Subcategory subcategory = subcategoryRepository
                .findById(subcategoryId)
                .orElseThrow(()->new ResourceNotFoundException("Subcategory not found with id : "+subcategoryId));

        if(!subcategory.isActive()){
            throw new ResourceNotFoundException("Subcategory not found with id : "+subcategoryId);
        }

        return subcategoryMapper.toResponse(subcategory);

    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryResponse> getSubcategoriesByCategory(Long categoryId) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category not found with id : "+categoryId));

        if(!category.isActive()){
            throw new ResourceNotFoundException("Category not found with id : "+categoryId);
        }

        return subcategoryRepository.findAllByCategoryId(categoryId)
                .stream()
                .filter(Subcategory::isActive)
                .map(subcategoryMapper::toResponse)
                .toList();

    }

    @Override
    public SubcategoryResponse updateSubcategory(Long subcategoryId, SubcategoryUpdateRequest request) {

     Subcategory subcategory = subcategoryRepository
             .findById(subcategoryId)
             .orElseThrow(()-> new ResourceNotFoundException("Subcategory not found with ID : "+subcategoryId));

     if(!subcategory.isActive()){
         throw new ResourceNotFoundException("Subcategory not found with ID : "+subcategoryId);
     }

     if(request.name()!=null && !request.name().equalsIgnoreCase(subcategory.getName())){

         subcategoryRepository.findByNameIgnoreCaseAndCategoryId(request.name(),subcategory.getCategory().getId())
                 .ifPresent(existing ->
                         {
                             if(existing.isActive()){
                                 throw new DuplicateResourceException("Subcategory already exists : "+request.name());
                             }
                         }
                         );
         subcategory.setName(request.name());
     }

     if(request.description()!=null){
         subcategory.setDescription(request.description());
     }

     Subcategory savedSubcategory = subcategoryRepository.save(subcategory);

     return subcategoryMapper.toResponse(savedSubcategory);

    }

    @Override
    public void deleteSubcategory(Long subcategoryId) {

        Subcategory subcategory = subcategoryRepository
                .findById(subcategoryId)
                .orElseThrow(()->new ResourceNotFoundException("Subcategory not found with ID : "+subcategoryId));

        if(!subcategory.isActive()){
            throw new ResourceNotFoundException("Subcategory not found with ID : "+subcategoryId);
        }

        subcategory.setActive(false);

        log.info("Subcategory soft deleted successfully: {}",subcategoryId);

    }
}
