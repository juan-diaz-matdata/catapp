package com.test.catapp.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class UserResponse {
    String id;
    String username;
    String email;
    String role;
    Instant createdAt;
}
