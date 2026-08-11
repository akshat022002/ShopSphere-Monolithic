package com.akshat.shopsphere.auth.controller;

import com.akshat.shopsphere.auth.dto.AuthResponse;
import com.akshat.shopsphere.auth.dto.LoginRequest;
import com.akshat.shopsphere.auth.dto.RefreshTokenRequest;
import com.akshat.shopsphere.auth.dto.RegisterRequest;
import com.akshat.shopsphere.auth.service.AuthService;
import com.akshat.shopsphere.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid
                                                              @RequestBody RegisterRequest request){

        AuthResponse authResponse = authService.register(request);

        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                true,
                "User registered successfully",
                authResponse,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);


    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid
                                                           @RequestBody LoginRequest request){

        AuthResponse response = authService.login(request);

        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(true
                ,"Login Successful"
                ,response
                ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid
                                                                  @RequestBody RefreshTokenRequest request){

       AuthResponse response = authService.refreshToken(request);

       return  ResponseEntity.ok(new ApiResponse<>(true
               ,"Token refreshed successfully"
               ,response
               ,LocalDateTime.now()
               ));

    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader){

        String token = authHeader.substring(7);
        authService.logout(token);

        ApiResponse<Void> response = new ApiResponse<>(true
                ,"Logged out successful"
                ,null,LocalDateTime.now() );

        return ResponseEntity.ok(response);

    }


}
