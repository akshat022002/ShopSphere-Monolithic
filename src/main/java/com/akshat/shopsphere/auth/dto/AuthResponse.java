package com.akshat.shopsphere.auth.dto;

public record AuthResponse(

        String accessToken,

        String refreshToken

) {
}
