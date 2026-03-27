package com.test.catapp.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Breed {
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