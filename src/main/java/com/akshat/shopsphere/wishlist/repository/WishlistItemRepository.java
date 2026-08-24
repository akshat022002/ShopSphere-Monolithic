package com.akshat.shopsphere.wishlist.repository;

import com.akshat.shopsphere.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem,Long> {

    Optional<WishlistItem> findByWishlistIdAndProductId(Long wishlistId,Long productId);

}


