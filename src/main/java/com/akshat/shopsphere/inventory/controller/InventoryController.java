package com.akshat.shopsphere.inventory.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.inventory.service.InventoryService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PatchMapping("/{productId}/increase")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> increaseStock(@PathVariable Long productId,
                                                           @RequestParam Integer quantity,
                                                           Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        inventoryService.increaseStock(productId,userDetails.getId(),userDetails.getRole(),quantity);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Stock increased successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{productId}/decrease")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> decreaseStock(@PathVariable Long productId,
                                                           @RequestParam Integer quantity,
                                                           Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        inventoryService.decreaseStock(productId,userDetails.getId(),userDetails.getRole(),quantity);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Stock decreased successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    public ResponseEntity<ApiResponse<Integer>> getStock(@PathVariable Long productId){

        Integer stock = inventoryService.getStock(productId);

        ApiResponse<Integer> apiResponse = new ApiResponse<>(true,
                "Stock fetched successfully",
                stock,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){

        return (UserDetailsImplementation) authentication.getPrincipal();

    }

}
