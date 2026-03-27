package com.test.catapp.application.service;

import com.test.catapp.domain.model.CatImage;
import com.test.catapp.domain.ports.in.ImageUseCase;
import com.test.catapp.domain.ports.out.CatApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ImageService implements ImageUseCase {

    private final CatApiPort catApiPort;

    @Override
    public Flux<CatImage> getImagesByBreedId(String breedId) {
        if (breedId == null || breedId.isBlank()) {
            return Flux.error(new IllegalArgumentException("Breed ID must not be blank"));
        }
        return catApiPort.fetchImagesByBreedId(breedId.trim());
    }
}
