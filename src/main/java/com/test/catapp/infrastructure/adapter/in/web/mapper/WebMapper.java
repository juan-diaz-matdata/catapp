package com.test.catapp.infrastructure.adapter.in.web.mapper;

import com.test.catapp.domain.model.Breed;
import com.test.catapp.domain.model.CatImage;
import com.test.catapp.domain.model.User;
import com.test.catapp.infrastructure.adapter.in.web.dto.BreedResponse;
import com.test.catapp.infrastructure.adapter.in.web.dto.ImageResponse;
import com.test.catapp.infrastructure.adapter.in.web.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebMapper {

    BreedResponse toResponse(Breed breed);

    ImageResponse toResponse(CatImage image);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toResponse(User user);
}
