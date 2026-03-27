package com.test.catapp.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String token;
    String tokenType;
    String username;
}
