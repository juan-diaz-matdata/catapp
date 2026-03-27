package com.test.catapp.application.service;

import com.test.catapp.domain.model.User;
import com.test.catapp.domain.ports.out.UserRepositoryPort;
import com.test.catapp.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        authService = new AuthService(userRepositoryPort, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void login_success() {
        User user = User.builder()
                .username("john")
                .passwordHash("hashed")
                .role(User.Role.USER)
                .build();

        when(userRepositoryPort.findByUsername("john")).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateToken("john", "USER")).thenReturn("jwt-token");

        StepVerifier.create(authService.login("john", "secret"))
                .expectNext("jwt-token")
                .verifyComplete();
    }

    @Test
    void login_userNotFound_returnsError() {
        when(userRepositoryPort.findByUsername("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(authService.login("unknown", "pass"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void login_wrongPassword_returnsError() {
        User user = User.builder()
                .username("john")
                .passwordHash("hashed")
                .role(User.Role.USER)
                .build();

        when(userRepositoryPort.findByUsername("john")).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        StepVerifier.create(authService.login("john", "wrong"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void register_success() {
        when(userRepositoryPort.existsByUsername("john")).thenReturn(Mono.just(false));
        when(userRepositoryPort.existsByEmail("john@test.com")).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("pass")).thenReturn("hashed");

        User savedUser = User.builder()
                .username("john")
                .email("john@test.com")
                .passwordHash("hashed")
                .role(User.Role.USER)
                .build();
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(authService.register("john", "john@test.com", "pass"))
                .expectNextMatches(u -> u.getUsername().equals("john"))
                .verifyComplete();
    }

    @Test
    void register_usernameTaken_returnsError() {
        when(userRepositoryPort.existsByUsername("john")).thenReturn(Mono.just(true));

        StepVerifier.create(authService.register("john", "john@test.com", "pass"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Username already taken"))
                .verify();
    }

    @Test
    void register_emailTaken_returnsError() {
        when(userRepositoryPort.existsByUsername("john")).thenReturn(Mono.just(false));
        when(userRepositoryPort.existsByEmail("john@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(authService.register("john", "john@test.com", "pass"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Email already registered"))
                .verify();
    }
}