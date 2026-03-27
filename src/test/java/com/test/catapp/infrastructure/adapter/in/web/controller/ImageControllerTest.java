package com.test.catapp.infrastructure.adapter.in.web.controller;

import com.test.catapp.domain.model.CatImage;
import com.test.catapp.domain.ports.in.ImageUseCase;
import com.test.catapp.infrastructure.adapter.in.web.dto.ImageResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ImageControllerTest {

    private ImageUseCase imageUseCase;
    private WebMapper webMapper;
    private ImageController imageController;

    @BeforeEach
    void setUp() {
        imageUseCase    = mock(ImageUseCase.class);
        webMapper       = mock(WebMapper.class);
        imageController = new ImageController(imageUseCase, webMapper);
    }

    @Test
    void getImagesByBreedId_validId_returnsImages() {
        CatImage img1 = CatImage.builder().id("img1").url("http://cat1.jpg").build();
        CatImage img2 = CatImage.builder().id("img2").url("http://cat2.jpg").build();

        ImageResponse resp1 = ImageResponse.builder().id("img1").url("http://cat1.jpg").build();
        ImageResponse resp2 = ImageResponse.builder().id("img2").url("http://cat2.jpg").build();

        when(imageUseCase.getImagesByBreedId("abys")).thenReturn(Flux.just(img1, img2));
        when(webMapper.toResponse(img1)).thenReturn(resp1);
        when(webMapper.toResponse(img2)).thenReturn(resp2);

        StepVerifier.create(imageController.getImagesByBreedId("abys"))
                .expectNext(resp1, resp2)
                .verifyComplete();
    }

    @Test
    void getImagesByBreedId_noImages_completesEmpty() {
        when(imageUseCase.getImagesByBreedId("abys")).thenReturn(Flux.empty());

        StepVerifier.create(imageController.getImagesByBreedId("abys"))
                .verifyComplete();
    }

    @Test
    void getImagesByBreedId_serviceError_propagatesError() {
        when(imageUseCase.getImagesByBreedId("  "))
                .thenReturn(Flux.error(new IllegalArgumentException("Breed ID must not be blank")));

        StepVerifier.create(imageController.getImagesByBreedId("  "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Breed ID must not be blank"))
                .verify();
    }
}