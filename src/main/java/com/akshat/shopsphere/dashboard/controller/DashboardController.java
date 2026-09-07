package com.akshat.shopsphere.dashboard.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.common.security.CurrentUserProvider;
import com.akshat.shopsphere.dashboard.dto.AdminDashboardResponse;
import com.akshat.shopsphere.dashboard.dto.SellerDashboardResponse;
import com.akshat.shopsphere.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard(){

        AdminDashboardResponse response = dashboardService.getAdminDashboard();

        ApiResponse<AdminDashboardResponse> apiResponse = new ApiResponse<>(
                true,
                "Admin dashboard fetched successfully",
                response,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);

    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<SellerDashboardResponse>> getSellerDashboard(Authentication authentication){


        SellerDashboardResponse response = dashboardService
                .getSellerDashboard(currentUserProvider.getUserId(authentication));

        ApiResponse<SellerDashboardResponse> apiResponse = new ApiResponse<>(
                true,
                "Seller dashboard fetched successfully",
                response,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);

    }
}
