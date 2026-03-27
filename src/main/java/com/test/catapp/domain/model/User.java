package com.test.catapp.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class User {
    String id;
    String username;
    String email;
    String passwordHash;
    Role role;
    Instant createdAt;

    public enum Role {
        USER, ADMIN
    }
}
