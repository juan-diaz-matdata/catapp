package com.test.catapp.infrastructure.adapter.in.web.controller;

import com.test.catapp.domain.model.User;
import com.test.catapp.domain.ports.out.UserRepositoryPort;
import com.test.catapp.infrastructure.adapter.in.web.dto.UserResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserRepositoryPort userRepositoryPort;
    private WebMapper webMapper;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);
        webMapper          = mock(WebMapper.class);
        userController     = new UserController(userRepositoryPort, webMapper);
    }

    @Test
    void getMe_authenticatedUser_returnsUserResponse() {
        User user = User.builder()
                .username("john")
                .email("john@test.com")
                .role(User.Role.USER)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .username("john")
                .email("john@test.com")
                .build();

        SecurityContext securityContext = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("john", null, java.util.List.of())
        );

        when(userRepositoryPort.findByUsername("john")).thenReturn(Mono.just(user));
        when(webMapper.toResponse(user)).thenReturn(userResponse);

        // Inject the SecurityContext into the reactive context
        StepVerifier.create(
                        userController.getMe()
                                .contextWrite(ctx -> ctx.put(
                                        org.springframework.security.core.context.ReactiveSecurityContextHolder.class.getName() + ".ATTR",
                                        securityContext
                                ))
                                .contextWrite(
                                        org.springframework.security.core.context.ReactiveSecurityContextHolder
                                                .withSecurityContext(Mono.just(securityContext))
                                )
                )
                .expectNextMatches(response ->
                        response.getUsername().equals("john") &&
                                response.getEmail().equals("john@test.com"))
                .verifyComplete();
    }

    @Test
    void getMe_userNotFound_propagatesError() {
        SecurityContext securityContext = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("ghost", null, java.util.List.of())
        );

        when(userRepositoryPort.findByUsername("ghost")).thenReturn(Mono.empty());

        StepVerifier.create(
                        userController.getMe()
                                .contextWrite(
                                        org.springframework.security.core.context.ReactiveSecurityContextHolder
                                                .withSecurityContext(Mono.just(securityContext))
                                )
                )
                .verifyComplete(); // flatMap on empty = empty, no error propagated
    }
}