package com.test.catapp.domain.ports.in;

import com.test.catapp.domain.model.User;
import reactor.core.publisher.Mono;

public interface AuthUseCase {
    Mono<String> login(String username, String password);

    Mono<User> register(String username, String email, String password);
}
