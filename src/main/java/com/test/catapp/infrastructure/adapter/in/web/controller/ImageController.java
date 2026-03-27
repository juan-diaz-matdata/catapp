package com.test.catapp.infrastructure.adapter.in.web.controller;


import com.test.catapp.domain.ports.in.ImageUseCase;
import com.test.catapp.infrastructure.adapter.in.web.dto.ImageResponse;
import com.test.catapp.infrastructure.adapter.in.web.mapper.WebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUseCase imageUseCase;
    private final WebMapper webMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<ImageResponse> getImagesByBreedId(@RequestParam("breed_id") String breedId) {
        return imageUseCase.getImagesByBreedId(breedId).map(webMapper::toResponse);
    }
}
