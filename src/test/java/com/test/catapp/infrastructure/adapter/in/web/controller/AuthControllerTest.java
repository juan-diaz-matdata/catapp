package com.test.catapp.infrastructure.adapter.in.web.controller;

import com.test.catapp.domain.model.User;
import com.test.catapp.domain.ports.in.AuthUseCase;
import com.test.catapp.infrastructure.adapter.in.web.dto.AuthResponse;
import com.test.catapp.infrastructure.adapter.in.web.dto.LoginRequest;
import com.test.catapp.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.test.catapp.infrastructure.adapter.in.web.dto.UserResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthUseCase authUseCase;
    private WebMapper webMapper;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authUseCase    = mock(AuthUseCase.class);
        webMapper      = mock(WebMapper.class);
        authController = new AuthController(authUseCase, webMapper);
    }
    @Test
    void login_validRequest_returnsAuthResponse() {
        LoginRequest request = LoginRequest.builder().username("john").password("secret").build();


        when(authUseCase.login("john", "secret")).thenReturn(Mono.just("jwt-token"));

        StepVerifier.create(authController.login(request))
                .expectNextMatches(response ->
                        response.getToken().equals("jwt-token") &&
                                response.getTokenType().equals("Bearer") &&
                                response.getUsername().equals("john"))
                .verifyComplete();
    }

    @Test
    void login_invalidCredentials_propagatesError() {
        LoginRequest request = LoginRequest.builder().username("john").password("wrong").build();

        when(authUseCase.login("john", "wrong"))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid credentials")));

        StepVerifier.create(authController.login(request))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Invalid credentials"))
                .verify();
    }


    @Test
    void register_validRequest_returnsUserResponse() {
        RegisterRequest request = RegisterRequest.builder().username("john").email("john@test.com").password("pass").build();


        User user = User.builder()
                .username("john")
                .email("john@test.com")
                .role(User.Role.USER)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .username("john")
                .email("john@test.com")
                .build();

        when(authUseCase.register("john", "john@test.com", "pass")).thenReturn(Mono.just(user));
        when(webMapper.toResponse(user)).thenReturn(userResponse);

        StepVerifier.create(authController.register(request))
                .expectNextMatches(response ->
                        response.getUsername().equals("john") &&
                                response.getEmail().equals("john@test.com"))
                .verifyComplete();
    }

    @Test
    void register_duplicateUsername_propagatesError() {
        RegisterRequest request = RegisterRequest.builder().username("john").email("john@test.com").password("pass").build();

        when(authUseCase.register("john", "john@test.com", "pass"))
                .thenReturn(Mono.error(new IllegalArgumentException("Username already taken")));

        StepVerifier.create(authController.register(request))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Username already taken"))
                .verify();
    }
}