package com.akshat.shopsphere.product.repository;

import com.akshat.shopsphere.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

}
