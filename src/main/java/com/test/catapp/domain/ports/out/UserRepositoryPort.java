package com.test.catapp.domain.ports.out;

import com.test.catapp.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);

    Mono<User> save(User user);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);
}
