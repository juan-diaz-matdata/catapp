package com.test.catapp.domain.ports.in;


import com.test.catapp.domain.model.CatImage;
import reactor.core.publisher.Flux;

public interface ImageUseCase {
    Flux<CatImage> getImagesByBreedId(String breedId);
}
