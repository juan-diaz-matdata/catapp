package com.test.catapp.infrastructure.adapter.in.web.controller;


import com.test.catapp.domain.ports.in.BreedUseCase;
import com.test.catapp.infrastructure.adapter.in.web.dto.BreedResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/breeds")
@RequiredArgsConstructor
public class BreedController {

    private final BreedUseCase breedUseCase;
    private final WebMapper webMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<BreedResponse> getAllBreeds() {
        return breedUseCase.getAllBreeds().map(webMapper::toResponse);
    }

    @GetMapping("/{breedId}")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<BreedResponse>> getBreedById(@PathVariable String breedId) {
        return breedUseCase.getBreedById(breedId)
                .map(webMapper::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public Flux<BreedResponse> searchBreeds(@RequestParam String query) {
        return breedUseCase.searchBreeds(query).map(webMapper::toResponse);
    }
}