package com.akshat.shopsphere.inventory.service;

import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final ProductRepository productRepository;

    @Override
    public void increaseStock(Long productId, Long userId, Role role, Integer quantity) {

        validateQuantity(quantity);

        Product product = getAuthorizedProduct(productId,userId,role);

        product.setStockQuantity(product.getStockQuantity()+quantity);

    }

    @Override
    public void decreaseStock(Long productId,Long userId,Role role, Integer quantity) {

        validateQuantity(quantity);

        Product product = getAuthorizedProduct(productId,userId,role);

        if(product.getStockQuantity()<quantity){
            throw new IllegalStateException("Insufficient stock for product : "+productId);
        }

        product.setStockQuantity(product.getStockQuantity()-quantity);
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getStock(Long productId) {

       Product product =getProduct(productId);

       return product.getStockQuantity();
    }

    @Override
    public void increaseStockForOrder(Long productId, Integer quantity) {

        validateQuantity(quantity);

        Product product = getProduct(productId);

        product.setStockQuantity(product.getStockQuantity()+quantity);
    }

    @Override
    public void decreaseStockForOrder(Long productId, Integer quantity) {

        validateQuantity(quantity);

        Product product = getProduct(productId);

        if(product.getStockQuantity()<quantity){
            throw new IllegalStateException("Insufficient stock for product : "+productId);
        }
        product.setStockQuantity(product.getStockQuantity()-quantity);
    }

    private Product getAuthorizedProduct(Long productId,Long userId ,Role role){

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new
                        ResourceNotFoundException("Product not found with id : "+productId));

        if(Role.ROLE_ADMIN.equals(role)){
            return product;
        }

        if(!product.getSeller().getId().equals(userId)){

            throw new AccessDeniedException("You are not authorized to update this product's stock");

        }
        return product;
    }

    private Product getProduct(Long productId){

        return productRepository.findById(productId)
                    .orElseThrow(()->
                        new ResourceNotFoundException("Product not found with id : "+productId));

    }

    private void validateQuantity(Integer quantity){

        if(quantity == null || quantity<=0){
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

    }


}
