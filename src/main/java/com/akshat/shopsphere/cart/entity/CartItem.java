package com.akshat.shopsphere.cart.entity;

import com.akshat.shopsphere.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items",
       uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_product",columnNames = {"cart_id","product_id"})
       },
       indexes = {
        @Index(name = "idx_cart_item_cart",columnList = "cart_id"),
        @Index(name = "idx_cart_item_product",columnList = "product_id")
       })
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

}
