package com.akshat.shopsphere.auth.dto;

import com.akshat.shopsphere.user.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Name is required")
    String name,

    @Email
    @NotBlank(message = "Email is required")
    String email,

    @NotBlank
    @Size(min = 8,max =10,message = "Password must be at least 8 characters")
    String password,

    Role role

) {
}
