package com.test.catapp.infrastructure.adapter.out.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageApiDto {
    private String id;
    private String url;
    private Integer width;
    private Integer height;
}