package com.test.catapp.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImageResponse {
    String id;
    String url;
    Integer width;
    Integer height;
}
