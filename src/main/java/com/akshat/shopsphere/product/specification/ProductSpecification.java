package com.akshat.shopsphere.product.specification;

import com.akshat.shopsphere.product.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private ProductSpecification(){}

    public static Specification<Product> filter(
            String name,
            Long categoryId,
            Long subcategoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice){

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isTrue(root.get("active")));

            if(name!=null && !name.isBlank()){

                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%"+name.toLowerCase()+"%"));

            }

            if(categoryId!=null){
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"),categoryId));
            }

            if(subcategoryId!=null){
                predicates.add(criteriaBuilder.equal(root.get("subcategory").get("id"),subcategoryId));
            }

            if(minPrice!=null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice));
            }

            if(maxPrice!=null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"),maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }

    }

