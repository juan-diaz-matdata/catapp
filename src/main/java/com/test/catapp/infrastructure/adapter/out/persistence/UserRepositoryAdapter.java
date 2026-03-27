package com.test.catapp.infrastructure.adapter.out.persistence;


import com.test.catapp.domain.model.User;
import com.test.catapp.domain.ports.out.UserRepositoryPort;
import com.test.catapp.infrastructure.adapter.out.persistence.mapper.UserDocumentMapper;
import com.test.catapp.infrastructure.adapter.out.persistence.repository.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserReactiveRepository repository;
    private final UserDocumentMapper mapper;

    @Override
    public Mono<User> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        return repository.save(mapper.toDocument(user)).map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}