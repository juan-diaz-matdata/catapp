package com.test.catapp.infrastructure.adapter.in.web.controller;


import com.test.catapp.domain.ports.in.AuthUseCase;
import com.test.catapp.infrastructure.adapter.in.web.dto.AuthResponse;
import com.test.catapp.infrastructure.adapter.in.web.dto.LoginRequest;
import com.test.catapp.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.test.catapp.infrastructure.adapter.in.web.dto.UserResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;
    private final WebMapper webMapper;

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authUseCase.login(request.getUsername(), request.getPassword())
                .map(token -> AuthResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .username(request.getUsername())
                        .build());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authUseCase.register(request.getUsername(), request.getEmail(), request.getPassword())
                .map(webMapper::toResponse);
    }
}
