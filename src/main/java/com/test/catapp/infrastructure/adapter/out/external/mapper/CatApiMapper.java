package com.test.catapp.infrastructure.adapter.out.external.mapper;

import com.test.catapp.domain.model.Breed;
import com.test.catapp.domain.model.CatImage;
import com.test.catapp.infrastructure.adapter.out.external.dto.BreedApiDto;
import com.test.catapp.infrastructure.adapter.out.external.dto.ImageApiDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatApiMapper {
    Breed toDomain(BreedApiDto dto);

    CatImage toDomain(ImageApiDto dto);
}
