package com.akshat.shopsphere.dashboard.service;

import com.akshat.shopsphere.dashboard.dto.AdminDashboardResponse;
import com.akshat.shopsphere.dashboard.dto.SellerDashboardResponse;

public interface DashboardService {

    AdminDashboardResponse getAdminDashboard();

    SellerDashboardResponse getSellerDashboard(Long sellerId);

}
