package com.akshat.shopsphere.cart.repository;

import com.akshat.shopsphere.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {


    Optional<CartItem> findByCartIdAndProductId(Long cartId,Long productId);
}
