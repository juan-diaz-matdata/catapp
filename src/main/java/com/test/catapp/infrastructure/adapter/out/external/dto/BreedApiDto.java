package com.test.catapp.infrastructure.adapter.out.external.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BreedApiDto {
    private String id;
    private String name;
    private String description;
    private String temperament;
    private String origin;

    @JsonProperty("life_span")
    private String lifeSpan;

    @JsonProperty("wikipedia_url")
    private String wikipediaUrl;

    private Integer adaptability;

    @JsonProperty("affection_level")
    private Integer affectionLevel;

    @JsonProperty("child_friendly")
    private Integer childFriendly;

    @JsonProperty("dog_friendly")
    private Integer dogFriendly;

    @JsonProperty("energy_level")
    private Integer energyLevel;

    private Integer grooming;

    @JsonProperty("health_issues")
    private Integer healthIssues;

    private Integer intelligence;

    @JsonProperty("shedding_level")
    private Integer sheddingLevel;

    @JsonProperty("social_needs")
    private Integer socialNeeds;

    @JsonProperty("stranger_friendly")
    private Integer strangerFriendly;

    private Integer vocalisation;
}