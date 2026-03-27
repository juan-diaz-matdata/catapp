package com.test.catapp.infrastructure.adapter.out.persistence.repository;

import com.test.catapp.infrastructure.adapter.out.persistence.entity.UserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveMongoRepository<UserDocument, String> {
    Mono<UserDocument> findByUsername(String username);

    Mono<UserDocument> findByEmail(String email);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);
}
