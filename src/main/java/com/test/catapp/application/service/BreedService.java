package com.test.catapp.application.service;


import com.test.catapp.domain.model.Breed;
import com.test.catapp.domain.ports.in.BreedUseCase;
import com.test.catapp.domain.ports.out.CatApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BreedService implements BreedUseCase {

    private final CatApiPort catApiPort;

    @Override
    public Flux<Breed> getAllBreeds() {
        return catApiPort.fetchAllBreeds();
    }

    @Override
    public Mono<Breed> getBreedById(String breedId) {
        if (breedId == null || breedId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Breed ID must not be blank"));
        }
        return catApiPort.fetchBreedById(breedId);
    }

    @Override
    public Flux<Breed> searchBreeds(String query) {
        if (query == null || query.isBlank()) {
            return Flux.error(new IllegalArgumentException("Search query must not be blank"));
        }
        return catApiPort.searchBreeds(query.trim());
    }
}
