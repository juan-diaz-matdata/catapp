package com.test.catapp.infrastructure.adapter.out.external.client;


import com.test.catapp.domain.model.Breed;
import com.test.catapp.domain.model.CatImage;
import com.test.catapp.domain.ports.out.CatApiPort;
import com.test.catapp.infrastructure.adapter.out.external.dto.BreedApiDto;
import com.test.catapp.infrastructure.adapter.out.external.dto.ImageApiDto;
import com.test.catapp.infrastructure.adapter.out.external.mapper.CatApiMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatApiClient implements CatApiPort {

    private final WebClient catWebClient;
    private final CatApiMapper catApiMapper;

    @Override
    @CircuitBreaker(name = "catApi", fallbackMethod = "fallbackBreeds")
    public Flux<Breed> fetchAllBreeds() {
        return catWebClient.get()
                .uri("/breeds")
                .retrieve()
                .bodyToFlux(BreedApiDto.class)
                .map(catApiMapper::toDomain)
                .doOnError(e -> log.error("Error fetching breeds: {}", e.getMessage()));
    }

    @Override
    @CircuitBreaker(name = "catApi", fallbackMethod = "fallbackBreed")
    public Mono<Breed> fetchBreedById(String breedId) {
        return catWebClient.get()
                .uri("/breeds/{id}", breedId)
                .retrieve()
                .bodyToMono(BreedApiDto.class)
                .map(catApiMapper::toDomain)
                .doOnError(e -> log.error("Error fetching breed {}: {}", breedId, e.getMessage()));
    }

    @Override
    @CircuitBreaker(name = "catApi", fallbackMethod = "fallbackBreedsSearch")
    public Flux<Breed> searchBreeds(String query) {
        return catWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/breeds/search").queryParam("q", query).build())
                .retrieve()
                .bodyToFlux(BreedApiDto.class)
                .map(catApiMapper::toDomain)
                .doOnError(e -> log.error("Error searching breeds '{}': {}", query, e.getMessage()));
    }

    @Override
    @CircuitBreaker(name = "catApi", fallbackMethod = "fallbackImages")
    public Flux<CatImage> fetchImagesByBreedId(String breedId) {
        return catWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/images/search")
                        .queryParam("breed_ids", breedId)
                        .queryParam("limit", 10)
                        .build())
                .retrieve()
                .bodyToFlux(ImageApiDto.class)
                .map(catApiMapper::toDomain)
                .doOnError(e -> log.error("Error fetching images for breed {}: {}", breedId, e.getMessage()));
    }

    // Fallback methods
    public Flux<Breed> fallbackBreeds(Throwable t) {
        log.warn("Circuit breaker: fallback for getAllBreeds - {}", t.getMessage());
        return Flux.empty();
    }

    public Mono<Breed> fallbackBreed(String breedId, Throwable t) {
        log.warn("Circuit breaker: fallback for getBreedById {} - {}", breedId, t.getMessage());
        return Mono.error(new RuntimeException("Cat API unavailable. Please try again later."));
    }

    public Flux<Breed> fallbackBreedsSearch(String query, Throwable t) {
        log.warn("Circuit breaker: fallback for searchBreeds '{}' - {}", query, t.getMessage());
        return Flux.empty();
    }

    public Flux<CatImage> fallbackImages(String breedId, Throwable t) {
        log.warn("Circuit breaker: fallback for getImagesByBreedId {} - {}", breedId, t.getMessage());
        return Flux.empty();
    }
}