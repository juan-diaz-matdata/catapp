package com.test.catapp.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BreedResponse {
    String id;
    String name;
    String description;
    String temperament;
    String origin;
    String lifeSpan;
    String wikipediaUrl;
    Integer adaptability;
    Integer affectionLevel;
    Integer childFriendly;
    Integer dogFriendly;
    Integer energyLevel;
    Integer grooming;
    Integer healthIssues;
    Integer intelligence;
    Integer sheddingLevel;
    Integer socialNeeds;
    Integer strangerFriendly;
    Integer vocalisation;
}