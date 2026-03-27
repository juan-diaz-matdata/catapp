package com.test.catapp.application.service;


import com.test.catapp.domain.model.User;
import com.test.catapp.domain.ports.in.AuthUseCase;
import com.test.catapp.domain.ports.out.UserRepositoryPort;
import com.test.catapp.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<String> login(String username, String password) {
        return userRepositoryPort.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid credentials")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        return Mono.error(new IllegalArgumentException("Invalid credentials"));
                    }
                    return Mono.just(jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name()));
                });
    }

    @Override
    public Mono<User> register(String username, String email, String password) {
        return userRepositoryPort.existsByUsername(username)
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        return Mono.error(new IllegalArgumentException("Username already taken"));
                    }
                    return userRepositoryPort.existsByEmail(email);
                })
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new IllegalArgumentException("Email already registered"));
                    }
                    User newUser = User.builder()
                            .username(username)
                            .email(email)
                            .passwordHash(passwordEncoder.encode(password))
                            .role(User.Role.USER)
                            .createdAt(Instant.now())
                            .build();
                    return userRepositoryPort.save(newUser);
                });
    }
}
