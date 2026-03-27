package com.test.catapp.application.service;

import com.test.catapp.domain.model.CatImage;
import com.test.catapp.domain.ports.out.CatApiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ImageServiceTest {

    private CatApiPort catApiPort;
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        catApiPort = mock(CatApiPort.class);
        imageService = new ImageService(catApiPort);
    }

    @Test
    void getImagesByBreedId_success() {
        CatImage image = CatImage.builder().id("img1").url("http://cat.jpg").build();
        when(catApiPort.fetchImagesByBreedId("abys")).thenReturn(Flux.just(image));

        StepVerifier.create(imageService.getImagesByBreedId("abys"))
                .expectNext(image)
                .verifyComplete();
    }

    @Test
    void getImagesByBreedId_blankId_returnsError() {
        StepVerifier.create(imageService.getImagesByBreedId("  "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Breed ID must not be blank"))
                .verify();
    }

    @Test
    void getImagesByBreedId_nullId_returnsError() {
        StepVerifier.create(imageService.getImagesByBreedId(null))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException)
                .verify();
    }
}