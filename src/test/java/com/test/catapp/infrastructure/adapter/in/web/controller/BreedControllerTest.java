package com.test.catapp.infrastructure.adapter.in.web.controller;

import com.test.catapp.domain.model.Breed;
import com.test.catapp.domain.ports.in.BreedUseCase;
import com.test.catapp.infrastructure.adapter.in.web.dto.BreedResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class BreedControllerTest {

    private BreedUseCase breedUseCase;
    private WebMapper webMapper;
    private BreedController breedController;

    @BeforeEach
    void setUp() {
        breedUseCase    = mock(BreedUseCase.class);
        webMapper       = mock(WebMapper.class);
        breedController = new BreedController(breedUseCase, webMapper);
    }

    // ── getAllBreeds ──────────────────────────────────────────────────────────

    @Test
    void getAllBreeds_returnsAllBreeds() {
        Breed breed1 = Breed.builder().id("abys").name("Abyssinian").build();
        Breed breed2 = Breed.builder().id("beng").name("Bengal").build();

        BreedResponse response1 = BreedResponse.builder().id("abys").name("Abyssinian").build();
        BreedResponse response2 = BreedResponse.builder().id("beng").name("Bengal").build();

        when(breedUseCase.getAllBreeds()).thenReturn(Flux.just(breed1, breed2));
        when(webMapper.toResponse(breed1)).thenReturn(response1);
        when(webMapper.toResponse(breed2)).thenReturn(response2);

        StepVerifier.create(breedController.getAllBreeds())
                .expectNext(response1, response2)
                .verifyComplete();
    }

    @Test
    void getAllBreeds_emptyResult_completesEmpty() {
        when(breedUseCase.getAllBreeds()).thenReturn(Flux.empty());

        StepVerifier.create(breedController.getAllBreeds())
                .verifyComplete();
    }

    // ── getBreedById ─────────────────────────────────────────────────────────

    @Test
    void getBreedById_existingId_returnsOk() {
        Breed breed = Breed.builder().id("abys").name("Abyssinian").build();
        BreedResponse breedResponse = BreedResponse.builder().id("abys").name("Abyssinian").build();

        when(breedUseCase.getBreedById("abys")).thenReturn(Mono.just(breed));
        when(webMapper.toResponse(breed)).thenReturn(breedResponse);

        StepVerifier.create(breedController.getBreedById("abys"))
                .expectNextMatches(entity ->
                        entity.getStatusCode().is2xxSuccessful() &&
                                entity.getBody() != null &&
                                entity.getBody().getId().equals("abys"))
                .verifyComplete();
    }

    @Test
    void getBreedById_notFound_returns404() {
        when(breedUseCase.getBreedById("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(breedController.getBreedById("unknown"))
                .expectNextMatches(entity -> entity.getStatusCode().value() == 404)
                .verifyComplete();
    }

    // ── searchBreeds ─────────────────────────────────────────────────────────

    @Test
    void searchBreeds_validQuery_returnsResults() {
        Breed breed = Breed.builder().id("abys").name("Abyssinian").build();
        BreedResponse breedResponse = BreedResponse.builder().id("abys").name("Abyssinian").build();

        when(breedUseCase.searchBreeds("aby")).thenReturn(Flux.just(breed));
        when(webMapper.toResponse(breed)).thenReturn(breedResponse);

        StepVerifier.create(breedController.searchBreeds("aby"))
                .expectNext(breedResponse)
                .verifyComplete();
    }

    @Test
    void searchBreeds_noResults_completesEmpty() {
        when(breedUseCase.searchBreeds("xyz")).thenReturn(Flux.empty());

        StepVerifier.create(breedController.searchBreeds("xyz"))
                .verifyComplete();
    }

    @Test
    void searchBreeds_serviceError_propagatesError() {
        when(breedUseCase.searchBreeds("  "))
                .thenReturn(Flux.error(new IllegalArgumentException("Search query must not be blank")));

        StepVerifier.create(breedController.searchBreeds("  "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Search query must not be blank"))
                .verify();
    }
}