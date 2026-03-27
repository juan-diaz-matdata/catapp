package com.test.catapp.application.service;

import com.test.catapp.domain.model.Breed;
import com.test.catapp.domain.ports.out.CatApiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class BreedServiceTest {

    private CatApiPort catApiPort;
    private BreedService breedService;

    @BeforeEach
    void setUp() {
        catApiPort = mock(CatApiPort.class);
        breedService = new BreedService(catApiPort);
    }

    @Test
    void getAllBreeds_returnsList() {
        Breed breed = Breed.builder().id("abys").name("Abyssinian").build();
        when(catApiPort.fetchAllBreeds()).thenReturn(Flux.just(breed));

        StepVerifier.create(breedService.getAllBreeds())
                .expectNext(breed)
                .verifyComplete();
    }

    @Test
    void getBreedById_success() {
        Breed breed = Breed.builder().id("abys").name("Abyssinian").build();
        when(catApiPort.fetchBreedById("abys")).thenReturn(Mono.just(breed));

        StepVerifier.create(breedService.getBreedById("abys"))
                .expectNext(breed)
                .verifyComplete();
    }

    @Test
    void getBreedById_blankId_returnsError() {
        StepVerifier.create(breedService.getBreedById("  "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Breed ID must not be blank"))
                .verify();
    }

    @Test
    void getBreedById_nullId_returnsError() {
        StepVerifier.create(breedService.getBreedById(null))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException)
                .verify();
    }

    @Test
    void searchBreeds_success() {
        Breed breed = Breed.builder().id("abys").name("Abyssinian").build();
        when(catApiPort.searchBreeds("aby")).thenReturn(Flux.just(breed));

        StepVerifier.create(breedService.searchBreeds("aby"))
                .expectNext(breed)
                .verifyComplete();
    }

    @Test
    void searchBreeds_blankQuery_returnsError() {
        StepVerifier.create(breedService.searchBreeds(""))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Search query must not be blank"))
                .verify();
    }
}