package com.akshat.shopsphere.wishlist.service;

import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.repository.UserRepository;
import com.akshat.shopsphere.wishlist.dto.WishlistResponse;
import com.akshat.shopsphere.wishlist.entity.Wishlist;
import com.akshat.shopsphere.wishlist.entity.WishlistItem;
import com.akshat.shopsphere.wishlist.mapper.WishlistItemMapper;
import com.akshat.shopsphere.wishlist.repository.WishlistItemRepository;
import com.akshat.shopsphere.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class WishlistServiceImpl implements WishlistService{

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistItemMapper wishlistItemMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public WishlistResponse getWishlist(Long userId) {

        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);

        if(wishlist==null){
            return new WishlistResponse(null, List.of());
        }

        return buildWishlistResponse(wishlist);
    }

    @Override
    public WishlistResponse addToWishlist(Long userId, Long productId) {

        Product product = productRepository.findById(productId).filter(Product::isActive)
                .orElseThrow(()->
                        new ResourceNotFoundException("Product not found with id : "+productId));

        Wishlist wishlist = getOrCreateWishlist(userId);

        boolean exists = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId)
                .isPresent();

        if(exists){
            throw new DuplicateResourceException("Product already exists in wishlist");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .product(product)
                .wishlist(wishlist)
                .build();

        wishlist.getItems().add(wishlistItem);

        wishlistRepository.save(wishlist);

        return buildWishlistResponse(wishlist);
    }


    @Override
    public void removeFromWishlist(Long userId, Long productId) {

        Wishlist wishlist = getWishlistForUser(userId);

        WishlistItem wishlistItem = wishlistItemRepository.
                findByWishlistIdAndProductId(wishlist.getId(),productId)
                .orElseThrow(()->new ResourceNotFoundException("Product is not in your wishlist"));

        wishlist.getItems().remove(wishlistItem);
    }

    @Override
    public void clearWishlist(Long userId) {

        Wishlist wishlist = getWishlistForUser(userId);

        wishlist.getItems().clear();

    }

    private WishlistResponse buildWishlistResponse(Wishlist wishlist){

        var items = wishlist.getItems()
                .stream()
                .map(wishlistItemMapper::toResponse)
                .toList();

        return new WishlistResponse(wishlist.getId(),items);
    }

    private Wishlist getOrCreateWishlist(Long userId){

        return  wishlistRepository.findByUserId(userId).orElseGet(()->{

            User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with id : "+userId));

            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .build();
            return  wishlistRepository.save(wishlist);
        }) ;
    }

    private Wishlist getWishlistForUser(Long userId){

        return wishlistRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Wishlist not found for user : "+userId));
    }

}
