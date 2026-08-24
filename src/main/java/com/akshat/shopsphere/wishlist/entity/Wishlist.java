package com.akshat.shopsphere.wishlist.entity;

import com.akshat.shopsphere.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wishlists",
       indexes = {
        @Index(name = "idx_wishlist_user",columnList = "user_id")
       })
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
                nullable = false,
                unique = true)
    private User user;

    @Column(nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();

    @OneToMany(mappedBy = "wishlist",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @Builder.Default
    private List<WishlistItem> items = new ArrayList<>();
}
