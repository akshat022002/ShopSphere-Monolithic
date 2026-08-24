package com.akshat.shopsphere.category.repository;

import com.akshat.shopsphere.category.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory,Long> {

    boolean existsByNameIgnoreCaseAndCategoryId(String name,Long categoryId);

    Optional<Subcategory> findByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    List<Subcategory> findAllByCategoryId(Long categoryId);
}
