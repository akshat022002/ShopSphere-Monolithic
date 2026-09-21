package com.akshat.shopsphere.cart.service;

import com.akshat.shopsphere.cart.dto.AddToCartRequest;
import com.akshat.shopsphere.cart.dto.CartItemResponse;
import com.akshat.shopsphere.cart.dto.CartResponse;
import com.akshat.shopsphere.cart.dto.UpdateCartItemRequest;
import com.akshat.shopsphere.cart.entity.Cart;
import com.akshat.shopsphere.cart.entity.CartItem;
import com.akshat.shopsphere.cart.mapper.CartItemMapper;
import com.akshat.shopsphere.cart.repository.CartItemRepository;
import com.akshat.shopsphere.cart.repository.CartRepository;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;

    @Transactional(readOnly = true)
    @Override
    public CartResponse getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if(cart==null){
            return new CartResponse(null, List.of(),BigDecimal.ZERO);
        }

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(Long userId, Long productId, AddToCartRequest request) {

        Product product = productRepository
                .findById(productId)
                .filter(Product::isActive).orElseThrow(()->
                        new ResourceNotFoundException("Product not found with id : "+productId));

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),productId).orElse(null);

        if(cartItem!=null){
            cartItem.setQuantity(cartItem.getQuantity()+request.quantity());
        } else {

            cartItem = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(request.quantity())
                    .build();

            cart.getItems().add(cartItem);
            cartRepository.save(cart);
        }
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {

       Cart cart = getCartForUser(userId);

       CartItem cartItem = cartItemRepository
               .findByCartIdAndProductId(cart.getId(),productId).orElseThrow(()->
                       new ResourceNotFoundException("Product is not in your cart"));

       cartItem.setQuantity(request.quantity());
       return buildCartResponse(cart);
    }

    @Override
    public void removeCartItem(Long userId, Long productId) {

        Cart cart = getCartForUser(userId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId).orElseThrow(()->
                        new ResourceNotFoundException("Product is not in your cart"));

        cart.getItems().remove(cartItem);

    }

    @Override
    public void clearCart(Long userId) {

        Cart cart = getCartForUser(userId);

        cart.getItems().clear();
    }

    private Cart getOrCreateCart(Long userId){

       return cartRepository.findByUserId(userId).orElseGet(()->{

           User user = userRepository.findById(userId).orElseThrow(()->
                   new ResourceNotFoundException("User not found with id : "+userId));

           Cart cart = Cart.builder()
                   .user(user)
                   .build();

           return cartRepository.save(cart);
       });
    }

    private Cart getCartForUser(Long userId){

        return cartRepository.findByUserId(userId).orElseThrow(()->
                new ResourceNotFoundException("Cart not found for user : "+userId));
    }

    private CartResponse buildCartResponse(Cart cart){

       var items = cart.getItems()
               .stream()
               .map(item->{
                   CartItemResponse response = cartItemMapper.toResponse(item);

                   BigDecimal subtotal = item.getProduct()
                           .getPrice()
                           .multiply(BigDecimal.valueOf(item.getQuantity()));

                   return new CartItemResponse(response.id(),
                           response.productId(),
                           response.productName(),
                           response.price(),
                           response.quantity(),
                           subtotal);


               }).toList();

       BigDecimal totalAmount = items.stream()
               .map(CartItemResponse::subtotal)
               .reduce(BigDecimal.ZERO,BigDecimal::add);

       return new CartResponse(cart.getId(),items,totalAmount);


    }
}
