package com.test.catapp.domain.ports.in;

import com.test.catapp.domain.model.Breed;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BreedUseCase {
    Flux<Breed> getAllBreeds();

    Mono<Breed> getBreedById(String breedId);

    Flux<Breed> searchBreeds(String query);
}

