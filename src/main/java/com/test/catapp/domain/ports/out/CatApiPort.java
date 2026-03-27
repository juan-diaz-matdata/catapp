package com.test.catapp.domain.ports.out;

import com.test.catapp.domain.model.Breed;
import com.test.catapp.domain.model.CatImage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatApiPort {
    Flux<Breed> fetchAllBreeds();

    Mono<Breed> fetchBreedById(String breedId);

    Flux<Breed> searchBreeds(String query);

    Flux<CatImage> fetchImagesByBreedId(String breedId);
}
