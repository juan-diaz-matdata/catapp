package com.test.catapp.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    String password;
}

