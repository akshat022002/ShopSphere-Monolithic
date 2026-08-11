package com.akshat.shopsphere.auth.service;

import com.akshat.shopsphere.auth.dto.AuthResponse;
import com.akshat.shopsphere.auth.dto.LoginRequest;
import com.akshat.shopsphere.auth.dto.RefreshTokenRequest;
import com.akshat.shopsphere.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken);

}
