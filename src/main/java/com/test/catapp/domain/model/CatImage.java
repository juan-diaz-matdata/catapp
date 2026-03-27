package com.test.catapp.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CatImage {
    String id;
    String url;
    Integer width;
    Integer height;
}

