package com.test.catapp.infrastructure.adapter.in.web.controller;

import com.test.catapp.domain.ports.out.UserRepositoryPort;
import com.test.catapp.infrastructure.adapter.in.web.dto.UserResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepositoryPort userRepositoryPort;
    private final WebMapper webMapper;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Mono<UserResponse> getMe() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userRepositoryPort::findByUsername)
                .map(webMapper::toResponse);
    }
}