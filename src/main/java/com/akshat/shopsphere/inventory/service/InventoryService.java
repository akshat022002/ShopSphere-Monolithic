package com.akshat.shopsphere.inventory.service;

import com.akshat.shopsphere.user.enums.Role;

public interface InventoryService {

    void increaseStock(Long productId, Long userId, Role role, Integer quantity);

    void decreaseStock(Long productId,Long userId,Role role, Integer quantity);

    Integer getStock(Long productId);

    void increaseStockForOrder(Long productId,Integer quantity);

    void decreaseStockForOrder(Long productId,Integer quantity);
}
